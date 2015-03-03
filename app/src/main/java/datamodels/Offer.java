package datamodels;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import utils.StringUtil;

/**
 * Created by Shamyyoun on 2/8/2015.
 */
public class Offer implements Serializable {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private String id;
    private String name;
    private String description;
    private double priceBefore;
    private double priceAfter;
    private Category category;
    private Date createDate;
    private Date updateDate;
    private Date expireDate;
    private String imageUrl;
    private boolean inCart;

    public Offer(String id) {
        this.id = id;
    }

    /**
     * method, used to calculate and return remain time
     */
    public long[] getRemainTime() {
        long current = Calendar.getInstance(Locale.getDefault()).getTimeInMillis();
        long expire = expireDate.getTime();

        // set default values
        long remain = 0;
        long seconds = 0;
        long minutes = 0;
        long hours = 0;
        long days = 0;

        // check current and expire
        if (expire > current) {
            remain = expire - current;
            seconds = remain / 1000 % 60;
            minutes = remain / (60 * 1000) % 60;
            hours = remain / (60 * 60 * 1000) % 24;
            days = remain / (24 * 60 * 60 * 1000);
        }

        return new long[]{seconds, minutes, hours, days};
    }

    public String getId() {
        return id;
    }

    public Offer setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Offer setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Offer setDescription(String description) {
        this.description = description;
        return this;
    }

    public double getPriceBefore() {
        return priceBefore;
    }

    public Offer setPriceBefore(double priceBefore) {
        this.priceBefore = priceBefore;
        return this;
    }

    public double getPriceAfter() {
        return priceAfter;
    }

    public Offer setPriceAfter(double priceAfter) {
        this.priceAfter = priceAfter;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public Offer setCategory(Category category) {
        this.category = category;
        return this;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getStrCreateDate() {
        return StringUtil.convertToString(createDate, DATE_FORMAT);
    }

    public Offer setCreateDate(String createDate) {
        this.createDate = StringUtil.convertToDate(createDate, DATE_FORMAT);
        return this;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Offer setUpdateDate(String updateDate) {
        this.updateDate = StringUtil.convertToDate(updateDate, DATE_FORMAT);
        return this;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public String getStrExpireDate() {
        return StringUtil.convertToString(expireDate, DATE_FORMAT);
    }

    public Offer setExpireDate(String expireDate) {
        this.expireDate = StringUtil.convertToDate(expireDate, DATE_FORMAT);
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Offer setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public boolean isInCart() {
        return inCart;
    }

    public void setInCart(boolean inCart) {
        this.inCart = inCart;
    }
}
