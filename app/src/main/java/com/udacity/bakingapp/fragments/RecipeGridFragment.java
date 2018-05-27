package com.udacity.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.bakingapp.R;
import com.udacity.bakingapp.activities.RecipeGridActivity;
import com.udacity.bakingapp.adapters.RecipeRecyclerViewAdapter;
import com.udacity.bakingapp.models.Recipe;

public class RecipeGridFragment extends Fragment {

    private static final String KEY_LAYOUT_MANAGER_STATE = "KEY_LAYOUT_MANAGER_STATE";
    private OnRecipeGridFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private Parcelable mSavedRecyclerLayoutState;

    public RecipeGridFragment() { }

    @SuppressWarnings("unused")
    public static RecipeGridFragment newInstance(int columnCount) {
        return new RecipeGridFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_grid, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;

            int columnCount = (int)Math.ceil((getResources().getConfiguration().screenWidthDp - 150) / 300.0);

            if (columnCount < 1)
                columnCount = 1;

            mGridLayoutManager = new GridLayoutManager(context, columnCount);
            mRecyclerView.setLayoutManager(mGridLayoutManager);

            mRecyclerView.setAdapter(new RecipeRecyclerViewAdapter(RecipeGridActivity.RecipeList, mListener, context));
        }
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null)
        {
            mSavedRecyclerLayoutState = savedInstanceState.getParcelable(KEY_LAYOUT_MANAGER_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        mSavedRecyclerLayoutState = mGridLayoutManager.onSaveInstanceState();
        outState.putParcelable(KEY_LAYOUT_MANAGER_STATE, mSavedRecyclerLayoutState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecipeGridFragmentInteractionListener) {
            mListener = (OnRecipeGridFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnRecipeGridFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void NotifyChange() {
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(new RecipeRecyclerViewAdapter(RecipeGridActivity.RecipeList, mListener, getActivity()));

            if (mSavedRecyclerLayoutState != null)
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedRecyclerLayoutState);
        }
    }

    public interface OnRecipeGridFragmentInteractionListener {
        void onRecipeGridFragmentInteraction(Recipe recipe);
    }
}
