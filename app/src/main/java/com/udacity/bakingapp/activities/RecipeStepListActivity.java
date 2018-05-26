package com.udacity.bakingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.bakingapp.R;
import com.udacity.bakingapp.fragments.RecipeStepDetailFragment;
import com.udacity.bakingapp.models.Recipe;
import com.udacity.bakingapp.models.RecipeStep;
import com.udacity.bakingapp.utilities.UserInterfaceUtils;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeStepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeStepListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private Recipe mRecipe;

    public static final String EXTRA_RECIPE = "EXTRA_RECIPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.recipe_detail_container) != null)
            mTwoPane = true;

        Intent intent = getIntent();

        if (savedInstanceState != null)
            mRecipe = savedInstanceState.getParcelable(EXTRA_RECIPE);
        else if (intent != null)
            mRecipe = intent.getParcelableExtra(EXTRA_RECIPE);

        if (mRecipe == null)
            closeOnError();

        setTitle(mRecipe.name);

        View recyclerView = findViewById(R.id.recipe_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_RECIPE, mRecipe);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void closeOnError() {
        UserInterfaceUtils.ShowToastMessage(getString(R.string.recipe_error_message), this);
        finish();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, mRecipe.steps, mTwoPane));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.RecipeStepViewHolder> {

        private final RecipeStepListActivity mParentActivity;
        private final RecipeStep[] mRecipeSteps;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    intent.putExtra(RecipeStepDetailFragment.ARG_RECIPE_STEP, recipeStep);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(RecipeStepListActivity parent,
                                      RecipeStep[] recipeSteps,
                                      boolean twoPane) {
            mRecipeSteps = recipeSteps;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        @NonNull
        public RecipeStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recipe_step, parent, false);
            return new RecipeStepViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecipeStepViewHolder holder, int position) {
            holder.mRecipeStepShortDescriptionTextView.setText(mRecipeSteps[position].shortDescription);

            holder.itemView.setTag(mRecipeSteps[position]);
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mRecipeSteps.length;
        }

        class RecipeStepViewHolder extends RecyclerView.ViewHolder {
            final TextView mRecipeStepShortDescriptionTextView;

            RecipeStepViewHolder(View view) {
                super(view);
                mRecipeStepShortDescriptionTextView = view.findViewById(R.id.recipe_step_short_description_text);
            }
        }
    }
}
