package com.udacity.bakingapp.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.udacity.bakingapp.R;
import com.udacity.bakingapp.activities.RecipeGridActivity;
import com.udacity.bakingapp.activities.RecipeStepListActivity;
import com.udacity.bakingapp.models.Recipe;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidget extends AppWidgetProvider {

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId, Recipe recipe) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);

        if (recipe != null) {
            views.setTextViewText(R.id.widget_recipe_name, recipe.name);
            views.setTextViewText(R.id.widget_recipe_ingredients, recipe.getIngredientsAsString());

            Intent intent = new Intent(context, RecipeStepListActivity.class);
            intent.putExtra(RecipeStepListActivity.EXTRA_RECIPE, recipe);
            PendingIntent pendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(intent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.widget_recipe_layout, pendingIntent);
        }
        else {
            Intent intent = new Intent(context, RecipeGridActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_recipe_layout, pendingIntent);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateRecipeWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, Recipe recipe) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipe);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, null);
        }
    }

    @Override
    public void onEnabled(Context context) { }

    @Override
    public void onDisabled(Context context) { }
}

