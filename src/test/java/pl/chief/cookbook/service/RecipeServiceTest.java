package pl.chief.cookbook.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import pl.chief.cookbook.features.RecipeCategory;
import pl.chief.cookbook.model.Ingredient;
import pl.chief.cookbook.model.Recipe;
import pl.chief.cookbook.repository.IngredientRepository;
import pl.chief.cookbook.repository.RecipeRepository;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
//@IfProfileValue(name="enableTests", value="true")
@SpringBootTest
public class RecipeServiceTest {
    private Ingredient ingredient;
    private Ingredient ingredient2;
    private Recipe recipe;
    private Recipe recipe2;
    private Recipe recipe3;
    private HashSet<Ingredient> ingredientSet;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    public void shouldFindRecipeByName() {
        recipeRepository.findByName("Margeritta").ifPresent(recipe -> assertEquals("Margeritta", recipe.getName()));
    }

    @Test
    public void shouldFindRecipeById() {
        recipeRepository.findByName("Margeritta").ifPresent(recipe -> assertEquals(1, recipe.getId()));
    }

    @Test
    public void shouldFindRecipesWithCaloriesIn() {
        int caloriesMin = 900;
        int caloriesMax = 1500;
        List<Recipe> recipeList = recipeRepository.findByCaloriesBetween(caloriesMin, caloriesMax);
        assertTrue(recipeList.stream().allMatch(r -> r.getCalories() > caloriesMin && r.getCalories() < caloriesMax));
    }

    @Test
    public void shouldFindRecipesWithChosenCategoryOnly() {
        RecipeCategory recipeCategory = RecipeCategory.CAKE_DESSERT;
        List<Recipe> recipeList = recipeRepository.findByRecipeCategory(recipeCategory);
        assertTrue(recipeList.stream().allMatch(r -> r.getRecipeCategory() == recipeCategory));
    }

    @Test
    @Transactional
    public void shouldFindAllRecipesContainingIngredient() {
        Ingredient randomIngredient = ingredientRepository.getOne(1);
        List<Recipe> recipes = recipeRepository.findByIngredientsContaining(randomIngredient);
        assertTrue(recipes.stream().allMatch(r ->
                r.getIngredients().contains(randomIngredient)));
    }

}
