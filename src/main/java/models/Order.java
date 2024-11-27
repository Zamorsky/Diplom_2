package models;

import lombok.AllArgsConstructor;

@AllArgsConstructor

public class Order {
    private String [] ingredients;

    public String[] getIngredients() {
        return ingredients;
    }
    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }
}