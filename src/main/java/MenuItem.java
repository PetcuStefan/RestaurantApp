public enum MenuItem {
    PIZZA_MARGHERITA("Pizza Margherita", 25.00),
    CHEESEBURGER("Cheeseburger", 20.00),
    CARBONARA("Carbonara", 22.00),
    GREEK_SALAD("Greek Salad", 15.00),
    LEMONADE("Lemonade", 8.00);

    private final String name;
    private final double price;

    MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name + " - " + price + " RON";
    }
}
