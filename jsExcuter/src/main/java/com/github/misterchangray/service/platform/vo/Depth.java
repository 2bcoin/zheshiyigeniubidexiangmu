package com.github.misterchangray.service.platform.vo;

public class Depth implements Comparable<Depth>{
    private Double price;
    private Double qty;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getQty() {
        return qty;
    }

    public Depth(Double price, Double qty) {
        this.price = price;
        this.qty = qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;

    }


    @Override
    public int compareTo(Depth o) {
        if(this.price > o.price) {
            return 1;
        } else if(this.price < o.price) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Depth{" +
                "price=" + price +
                ", qty=" + qty +
                '}';
    }
}