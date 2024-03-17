public class ShoppingCart {

    private List<Item> items;
    private double cartValue;

    public ShoppingCart() {
        this.items = new ArrayList<>();
        this.cartValue = 0.0;
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public double calculateCartValue() {
        this.cartValue = items.stream().mapToDouble(Item::getPrice).sum();
        return this.cartValue;
    }

    public double applyFlatAmountOffer(FlatAmountOffer offer) {
        for (Item item : items) {
            if (offer.isApplicable(item)) {
                item.applyFlatAmountDiscount(offer.getAmount());
            }
        }
        return calculateCartValue();
    }

    public double applyFlatPercentageOffer(FlatPercentageOffer offer) {
        for (Item item : items) {
            if (offer.isApplicable(item)) {
                item.applyFlatPercentageDiscount(offer.getPercentage());
            }
        }
        return calculateCartValue();
    }

    public static void main(String[] args) {
        // Create items
        Item item1 = new Item("Item 1", 10.0);
        Item item2 = new Item("Item 2", 20.0);

        // Create shopping cart and add items
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(item1);
        cart.addItem(item2);

        // Create and apply offers for different customer segments
        FlatAmountOffer offer1 = new FlatAmountOffer("OFFER1", 5.0); // $5 off
        FlatPercentageOffer offer2 = new FlatPercentageOffer("OFFER2", 0.2); // 20% off

        // Apply offers for different segments
        cart.applyFlatAmountOffer(offer1);
        cart.applyFlatPercentageOffer(offer2);

        // Calculate final cart value
        double finalCartValue = cart.calculateCartValue();
        System.out.println("Final Cart Value: $" + finalCartValue);
    }
}

class Item {
    private String name;
    private double price;

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void applyFlatAmountDiscount(double amount) {
        this.price -= amount;
    }

    public void applyFlatPercentageDiscount(double percentage) {
        this.price -= this.price * percentage;
    }
}

class FlatAmountOffer {
    private String name;
    private double amount;

    public FlatAmountOffer(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }

    public boolean isApplicable(Item item) {
        // You can implement logic to check if the offer is applicable to the item
        return true;
    }

    public double getAmount() {
        return amount;
    }
}

class FlatPercentageOffer {
    private String name;
    private double percentage;

    public FlatPercentageOffer(String name, double percentage) {
        this.name = name;
        this.percentage = percentage;
    }

    public boolean isApplicable(Item item) {
        // You can implement logic to check if the offer is applicable to the item
        return true;
    }

    public double getPercentage() {
        return percentage;
    }
}
