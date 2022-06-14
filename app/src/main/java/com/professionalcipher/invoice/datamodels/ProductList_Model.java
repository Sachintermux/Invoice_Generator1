package com.professionalcipher.invoice.datamodels;

public class ProductList_Model {
    private String productName = "";
    private String price = "";
    private String quantity = "";
    private String discount = "";
    private String tax = "";
    private String type = "";
    private String tax_price = "";
    private String discount_price = "";
    private String finalPrice = "";
    private String itemCode = "";

    public ProductList_Model( String productName, String price, String quantity, String discount,
                              String tax, String type, String tax_price, String discount_price, String finalPrice, String itemCode ) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
        this.tax = tax;
        this.type = type;
        this.tax_price = tax_price;
        this.discount_price = discount_price;
        this.finalPrice = finalPrice;
        this.itemCode = itemCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode( String itemCode ) {
        this.itemCode = itemCode;
    }

    public ProductList_Model( String productName, String price, String quantity, String discount, String tax, String type, String tax_price, String discount_price, String finalPrice ) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
        this.tax = tax;
        this.type = type;
        this.tax_price = tax_price;
        this.discount_price = discount_price;
        this.finalPrice = finalPrice;
    }

    public ProductList_Model( String productName, String price, String quantity, String discount, String tax, String type ) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
        this.tax = tax;
        this.type = type;
    }

    public ProductList_Model() {

    }

    public ProductList_Model( String productName, String price, String quantity, String discount, String tax ) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
        this.tax = tax;
    }

    public String getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice( String finalPrice ) {
        this.finalPrice = finalPrice;
    }

    public String getTax_price() {
        return tax_price;
    }

    public void setTax_price( String tax_price ) {
        this.tax_price = tax_price;
    }

    public String getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price( String discount_price ) {
        this.discount_price = discount_price;
    }

    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName( String productName ) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice( String price ) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity( String quantity ) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount( String discount ) {
        this.discount = discount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax( String tax ) {
        this.tax = tax;
    }
}
