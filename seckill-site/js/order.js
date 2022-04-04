$(document).ready(function () {
    get_items();
    // 加载类型订单
    $("#btn-order-all").click(function (e) { get_items(); });
    $("#btn-order-0").click(function (e) { get_items(0); });
    $("#btn-order-1").click(function (e) { get_items(1); });
    $("#btn-order-2").click(function (e) { get_items(2); });
    $("#btn-order-3").click(function (e) { get_items(3); });
});

function get_items(status) {
    var url = SERVER_PATH + "/order/list?token=" + window.sessionStorage.getItem("token");
    switch (status) {
        case undefined:
            break;
        default:
            url = url + "&status=" + status;
            break;
    }
    $.ajax({
        type: "GET",
        url: url,
        xhrFields: { withCredentials: true },
        success: function (result) {
            if (result.status) {
                alertBox(result.data.message);
                return false;
            }
            set_items(result.data);
        }
    });
}

function set_items(items) {
    if (!items) {
        return false;
    }

    // 清空内容
    $(".order-list").empty();
    $.each(items, function (i, item) {
        // 定义模板
        var div =
            '<div class="thumbnail order">' +
            '<div class="order-time"><span> 下单时间：</span><span> <b></b></div>' +
            '<div class="order-status"><span> 订单状态：</span><span> <b></b></div>' +
            '<div class="order-link"><a href="item.html"><img ' +
            'src="" />' +
            '<div class="order-name"><b></b></div>' +
            '</a></div>' +
            '<div class="order-price">' +
            '<span> 商品价格：￥</span>' +
            '<span> <b></b>' +
            '</div>' +
            '<div class="order-amount">' +
            '<span> 商品数量：x</span>' +
            '<span> <b></b>' +
            '</div>' +
            '<div class="order-total">' +
            '<span> 总金额:￥</span>' +
            '<span> <b></b>' +
            '</div>' +
            '<div class="order-number">' +
            '<span> 订单编号:</span>' +
            '<span> <b></b>' +
            '</div>' +
            '<div class="order-address">' +
            '<span> 地址信息:</span>' +
            '<span> <b></b>' +
            '</div>' +
            '<div class="order-shipment">' +
            '<span> 物流编号:</span>' +
            '<span> <b></b>' +
            '</div>' +
            '<div class="order-permit">' +
            '<a class="btn btn-danger" href="javascript:void(0)"></a>' +
            '</div>' +
            '</div>';

        // 设置数据
        var $obj = $(div);
        $obj.find(".order-link a").attr("href", "item.html?id=" + item.itemId);
        //$obj.find(".order-link img").attr("src", item.imageUrl);
        var detail = get_detail(item.itemId);
        console.log(detail)
        $obj.find(".order-link img").attr("src", detail.imageUrl);
        $obj.find(".order-name b").text(detail.title);
        $obj.find(".order-time b").text(item.orderTime.substring(0,item.orderTime.indexOf('.')).replace(/T/," "));
        $obj.find(".order-price b").text(item.orderPrice);
        $obj.find(".order-amount b").text(item.orderAmount);
        $obj.find(".order-total b").text(item.orderTotal);
        $obj.find(".order-number b").text(item.id);
        $obj.find(".order-address b").text(item.receiverName + " " + item.receiverPhone + " " + item.receiverAddress);
        switch (item.orderStatus) {
            case 0:
                $obj.find(".order-status b").text("待支付");
                $obj.find(".order-permit a").text("付款").attr("onclick", "update_status('" + item.id+"',1)");
                break;
            case 1:
                $obj.find(".order-status b").text("待发货");
                $obj.find(".order-permit").hide();
                break;
            case 2:
                $obj.find(".order-status b").text("待收货");
                $obj.find(".order-permit a").text("确认收货").attr("onclick", "update_status('" + item.id+"',3)");
                break;
            default:
                $obj.find(".order-status b").text("已完成");
                $obj.find(".order-permit").hide();
                break;
        }
        if (item.shipmentNumber)
            $obj.find(".order-shipment b").text(item.shipmentNumber);
        else
            $obj.find(".order-shipment").hide();

        // 显示内容
        $(".order-list").append($obj);
    });

}

function get_detail(itemId) {
    var returnData;
    $.ajax({
        type: "GET",
        url: SERVER_PATH + "/item/detail/" + itemId,
        xhrFields: {withCredentials: true},
        async: false,
        success: function (result) {
            if (result.status) {
                alertBox(result.data.message);
                return false;
            }
            returnData = result.data;
        }
    });
    return returnData;
}
function update_status(orderId,orderStatus){
    $.ajax({
        type: "POST",
        url: SERVER_PATH + "/order/status?token=" + window.sessionStorage.getItem("token"),
        data: {
            "orderId": orderId,
            "orderStatus": orderStatus,
        },
        xhrFields: {withCredentials: true},
        success: function (result) {
            if (result.status) {
                alertBox(result.data.message);
                return false;
            }
            alertBox("成功！", function(){
                location.href = "./order.html"
            });
        }
    });
}