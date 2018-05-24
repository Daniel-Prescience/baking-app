package com.udacity.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.bakingapp.R;
import com.udacity.bakingapp.fragments.RecipeGridFragment.OnRecipeGridFragmentInteractionListener;
import com.udacity.bakingapp.models.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.bakingapp.utilities.UserInterfaceUtils.ShowToastMessage;

public class RecipeRecyclerViewAdapter extends RecyclerView.Adapter<RecipeRecyclerViewAdapter.ViewHolder> {
    private final Recipe[] mRecipes;
    private final OnRecipeGridFragmentInteractionListener mListener;
    private final Context mContext;

    public RecipeRecyclerViewAdapter(Recipe[] recipes, OnRecipeGridFragmentInteractionListener listener, Context activityContext) {
        mRecipes = recipes;
        mListener = listener;
        mContext = activityContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mRecipes[position];
            Picasso.with(mContext)
                    .load(holder.mItem.image)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.mRecipeImageView);

        holder.mRecipeImageView.setContentDescription(holder.mItem.name);
        holder.mRecipeNameTextView.setText(holder.mItem.name);

        // TODO: If recipe id equals the one stored in shared preferences, set as favorite.
        if (holder.mItem.id == 1)
            holder.mRecipeSetAsWidgetImageButton.setImageResource(R.drawable.ic_favorite_black_24dp);

        holder.mRecipeSetAsWidgetImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement setting recipe id in shared preference, which then should be read in the above todo.
                ShowToastMessage(holder.mItem.name + " set as widget recipe.", mContext);
            }
        });

        holder.mRecipeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onRecipeGridFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null)
            return 0;

        return mRecipes.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recipeCardView)
        CardView mRecipeCardView;

        @BindView(R.id.recipeImageView)
        ImageView mRecipeImageView;

        @BindView(R.id.recipeNameTextView)
        TextView mRecipeNameTextView;

        @BindView(R.id.recipeSetAsWidgetImageButton)
        ImageButton mRecipeSetAsWidgetImageButton;

        Recipe mItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
