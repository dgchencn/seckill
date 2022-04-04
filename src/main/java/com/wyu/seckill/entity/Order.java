package com.wyu.seckill.entity;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Order {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.id
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    private String id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.user_id
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    private Integer userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.item_id
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    private Integer itemId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.promotion_id
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    private Integer promotionId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.order_price
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    private BigDecimal orderPrice;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.order_amount
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    private Integer orderAmount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.order_total
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    private BigDecimal orderTotal;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.order_time
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    private Timestamp orderTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.order_status
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    private Integer orderStatus;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.receiver_name
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    @NotBlank(message = "收货人不能为空")
    private String receiverName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.receiver_phone
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    @NotBlank(message = "收货电话不能为空")
    private String receiverPhone;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.receiver_address
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column order_info.shipment_number
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    private String shipmentNumber;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.id
     *
     * @return the value of order_info.id
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.id
     *
     * @param id the value for order_info.id
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.user_id
     *
     * @return the value of order_info.user_id
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.user_id
     *
     * @param userId the value for order_info.user_id
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.item_id
     *
     * @return the value of order_info.item_id
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.item_id
     *
     * @param itemId the value for order_info.item_id
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.promotion_id
     *
     * @return the value of order_info.promotion_id
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public Integer getPromotionId() {
        return promotionId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.promotion_id
     *
     * @param promotionId the value for order_info.promotion_id
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.order_price
     *
     * @return the value of order_info.order_price
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.order_price
     *
     * @param orderPrice the value for order_info.order_price
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.order_amount
     *
     * @return the value of order_info.order_amount
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public Integer getOrderAmount() {
        return orderAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.order_amount
     *
     * @param orderAmount the value for order_info.order_amount
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public void setOrderAmount(Integer orderAmount) {
        this.orderAmount = orderAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.order_total
     *
     * @return the value of order_info.order_total
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.order_total
     *
     * @param orderTotal the value for order_info.order_total
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public void setOrderTotal(BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.order_time
     *
     * @return the value of order_info.order_time
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public Timestamp getOrderTime() {
        return orderTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.order_time
     *
     * @param orderTime the value for order_info.order_time
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.order_status
     *
     * @return the value of order_info.order_status
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public Integer getOrderStatus() {
        return orderStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.order_status
     *
     * @param orderStatus the value for order_info.order_status
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.receiver_name
     *
     * @return the value of order_info.receiver_name
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public String getReceiverName() {
        return receiverName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.receiver_name
     *
     * @param receiverName the value for order_info.receiver_name
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName == null ? null : receiverName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.receiver_phone
     *
     * @return the value of order_info.receiver_phone
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public String getReceiverPhone() {
        return receiverPhone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.receiver_phone
     *
     * @param receiverPhone the value for order_info.receiver_phone
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone == null ? null : receiverPhone.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.receiver_address
     *
     * @return the value of order_info.receiver_address
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public String getReceiverAddress() {
        return receiverAddress;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.receiver_address
     *
     * @param receiverAddress the value for order_info.receiver_address
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress == null ? null : receiverAddress.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column order_info.shipment_number
     *
     * @return the value of order_info.shipment_number
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public String getShipmentNumber() {
        return shipmentNumber;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column order_info.shipment_number
     *
     * @param shipmentNumber the value for order_info.shipment_number
     *
     * @mbg.generated Thu Nov 25 16:44:20 CST 2021
     */
    public void setShipmentNumber(String shipmentNumber) {
        this.shipmentNumber = shipmentNumber == null ? null : shipmentNumber.trim();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", itemId=" + itemId +
                ", promotionId=" + promotionId +
                ", orderPrice=" + orderPrice +
                ", orderAmount=" + orderAmount +
                ", orderTotal=" + orderTotal +
                ", orderTime=" + orderTime +
                ", orderStatus=" + orderStatus +
                ", receiverName='" + receiverName + '\'' +
                ", receiverPhone='" + receiverPhone + '\'' +
                ", receiverAddress='" + receiverAddress + '\'' +
                ", shipmentNumber='" + shipmentNumber + '\'' +
                '}';
    }
}