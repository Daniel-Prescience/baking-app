package com.udacity.bakingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.bakingapp.R;
import com.udacity.bakingapp.activities.RecipeStepDetailActivity;
import com.udacity.bakingapp.activities.RecipeStepListActivity;
import com.udacity.bakingapp.fragments.RecipeStepDetailFragment;
import com.udacity.bakingapp.models.Recipe;
import com.udacity.bakingapp.models.RecipeStep;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.bakingapp.activities.RecipeStepListActivity.EXTRA_RECIPE;
import static com.udacity.bakingapp.activities.RecipeStepListActivity.EXTRA_RECIPE_CURRENT_STEP;

public class RecipeStepListRecyclerViewAdapter extends RecyclerView.Adapter<RecipeStepListRecyclerViewAdapter.RecipeInstructionViewHolder> {

    private final RecipeStepListActivity mParentActivity;
    private final Recipe mRecipe;
    private final boolean mTwoPane;

    private View.OnClickListener getRecipeStepItemClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyItemChanged(mParentActivity.mCurrentStepIndex);
                mParentActivity.mCurrentStepIndex = position;
                notifyItemChanged(mParentActivity.mCurrentStepIndex);
                RecipeStep recipeStep = (RecipeStep) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(RecipeStepDetailFragment.ARG_RECIPE_STEP, recipeStep);
                    RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipe_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, RecipeStepDetailActivity.class);
                    intent.putExtra(EXTRA_RECIPE, mRecipe);
                    intent.putExtra(EXTRA_RECIPE_CURRENT_STEP, position);

                    context.startActivity(intent);
                }
            }
        };
    }

    public RecipeStepListRecyclerViewAdapter(RecipeStepListActivity parent, Recipe recipe, boolean twoPane) {
        mRecipe = recipe;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public int getItemCount() {
        // Item count is the number of steps + 1 item for the ingredients.
        return mRecipe.steps.length + 1;
    }

    @Override
    @NonNull
    public RecipeInstructionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_instruction, parent, false);
        return new RecipeInstructionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeInstructionViewHolder holder, int position) {
        if (mParentActivity.mTwoPane)
            holder.recipeCardView.setCardBackgroundColor(mParentActivity.mCurrentStepIndex == position ? Color.LTGRAY : Color.WHITE);

        if (position == 0) {
            // Ingredients item
            holder.mRecipeInstructionsTextView.setText(R.string.ingredients);
            RecipeStep ingredientsAsRecipeStep = new RecipeStep(
                    (long)0,
                    mParentActivity.getResources().getString(R.string.ingredients), mRecipe.getIngredientsAsString(),
                    null,
                    mRecipe.image);
            holder.itemView.setTag(ingredientsAsRecipeStep);
            holder.itemView.setOnClickListener(getRecipeStepItemClickListener(position));
        } else {
            // Step item
            holder.mRecipeInstructionsTextView.setText(mRecipe.steps[position - 1].shortDescription);
            holder.itemView.setTag(mRecipe.steps[position - 1]);
            holder.itemView.setOnClickListener(getRecipeStepItemClickListener(position));
        }
    }

    class RecipeInstructionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recipeCardView) CardView recipeCardView;
        @BindView(R.id.recipe_instruction_text) TextView mRecipeInstructionsTextView;

        RecipeInstructionViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
