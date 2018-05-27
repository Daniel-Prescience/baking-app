package com.udacity.bakingapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.udacity.bakingapp.R;
import com.udacity.bakingapp.activities.RecipeStepDetailActivity;
import com.udacity.bakingapp.activities.RecipeStepListActivity;
import com.udacity.bakingapp.models.RecipeStep;

/**
 * A fragment representing a single Recipe detail screen.
 * This fragment is either contained in a {@link RecipeStepListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeStepDetailActivity}
 * on handsets.
 */
public class RecipeStepDetailFragment extends Fragment implements Player.EventListener {

    public static final String ARG_RECIPE_STEP = "RECIPE_STEP";
    private static final String PLAYER_POSITION = "PLAYER_POSITION";
    private static final String PLAYER_STATE = "PLAYER_STATE";
    private static final String PLAYER_FULLSCREEN = "PLAYER_FULLSCREEN";
    private static final String TAG = RecipeStepDetailFragment.class.getSimpleName();

    private FragmentActivity mActivity;
    private RecipeStep mRecipeStep;

    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    private long mPlayerPosition;
    private boolean mPlayState = true;
    private boolean mPlayerFullscreen = false;

    private Dialog mFullScreenDialog;

    public RecipeStepDetailFragment() {
    }

    //region Inspired by https://stackoverflow.com/questions/14999698/android-how-to-notify-activity-when-fragments-views-are-ready#answer-15007656
    public interface OnCompleteListener {
        void onFragmentAttachComplete();
    }

    private OnCompleteListener mListener;

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnCompleteListener)context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }
    //endregion

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipe_step_detail, container, false);

        mActivity = getActivity();

        if (savedInstanceState != null) {
            mPlayerPosition = savedInstanceState.getLong(PLAYER_POSITION);
            mPlayState = savedInstanceState.getBoolean(PLAYER_STATE);
            mPlayerFullscreen = savedInstanceState.getBoolean(PLAYER_FULLSCREEN);
        }

        Bundle fragmentArguments = getArguments();

        if (fragmentArguments != null && fragmentArguments.containsKey(ARG_RECIPE_STEP))
            mRecipeStep = fragmentArguments.getParcelable(ARG_RECIPE_STEP);

        if (mRecipeStep != null) {
            TextView recipeDetailTextView = rootView.findViewById(R.id.recipe_step_detail_text);
            recipeDetailTextView.setText(mRecipeStep.description);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putLong(PLAYER_POSITION, mPlayerPosition);
        outState.putBoolean(PLAYER_STATE, mPlayState);
        outState.putBoolean(PLAYER_FULLSCREEN, mPlayerFullscreen);

        super.onSaveInstanceState(outState);
    }

    private void initializePlayer() {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(mActivity, trackSelector);
            mPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(mActivity, mActivity.getPackageName());

            if (!TextUtils.isEmpty(mRecipeStep.videoURL)) {
                ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(mActivity, userAgent));
                MediaSource mediaSource = factory.createMediaSource(Uri.parse(mRecipeStep.videoURL));
                mExoPlayer.prepare(mediaSource);
                mExoPlayer.setPlayWhenReady(mPlayState);
                mExoPlayer.seekTo(mPlayerPosition);
            } else
                mPlayerView.setVisibility(View.GONE);
        }
    }

    private void initFullscreenDialog() {
        mFullScreenDialog = new Dialog(mActivity, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                mActivity.finish();
            }
        };
    }

    //region Inspiration found in course material project: ClassicalMusicQuiz.
    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {
        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(mActivity, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }
    //endregion

    @Override
    public void onResume() {
        super.onResume();

        initializeMediaSession();

        if (mPlayerView == null) {
            mPlayerView = mActivity.findViewById(R.id.playerView);
            initFullscreenDialog();
        }

        mPlayerFullscreen = mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && !(getResources().getConfiguration().screenWidthDp >= 900);

        // Load cake image as default artwork.
        Target mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mPlayerView.setDefaultArtwork(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        Picasso.with(getContext())
                .load(!TextUtils.isEmpty(mRecipeStep.thumbnailURL) ? mRecipeStep.thumbnailURL : mRecipeStep.fallbackImageURL)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.drawable.ic_launcher_background)
                .into(mTarget);

        initializePlayer();

        if (mPlayerFullscreen && !TextUtils.isEmpty(mRecipeStep.videoURL)) {
            ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
            mFullScreenDialog.addContentView(mPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullScreenDialog.show();

            Window window = mFullScreenDialog.getWindow();

            if (window != null) {
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
            }
        }

        // Signal parent Activity that we're done setting up fragment views.
        mListener.onFragmentAttachComplete();
    }


    @Override
    public void onPause() {

        super.onPause();

        if (mPlayerView != null && mPlayerView.getPlayer() != null) {
            Player player = mPlayerView.getPlayer();
            mPlayerPosition = player.getCurrentPosition();
            mPlayState = player.getPlayWhenReady();

            player.stop();
            player.release();
        }

        if (mMediaSession != null)
            mMediaSession.setActive(false);

        if (mFullScreenDialog != null)
            mFullScreenDialog.dismiss();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == Player.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}