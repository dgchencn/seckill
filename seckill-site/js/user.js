$(document).ready(function () {
    // 设置用户登录状态
    set_login_status();
    // 设置用户地址信息
    set_address_data();
    // 地址管理按钮单击事件
    $("#btn-address").click(function (e) { 
        address();        
    });
    // 编辑按钮单击事件
    $("#btn-edit-address").click(function (e) { 
        edit_address();        
    });
    // 注销按钮单击事件
    $("#btn-logout").click(function (e) { 
        logout();        
    });
});

// 监听jQuery的ajax事件
// $(document).ajaxSend(function(event, jqXHR, ajaxOptions){
//     ajaxOptions.url += ajaxOptions.url.match(/\?/) ? "&" : "?";
//     ajaxOptions.url += "token=" + window.localStorage.getItem("token");
// });

function set_login_status() {
    var $A = $(".user-name");
    if (!$A) return false;

    $.ajax({
        type: "GET",
        url: SERVER_PATH + "/user/status?token=" + window.sessionStorage.getItem("token"),
        xhrFields: {withCredentials: true},
        success: function (result) {
            if (result.status == "0" && result.data) {
                $A.text(result.data.nickname);
                $("#user-info").show();
                $("#order").show();
            } else {
                $("#user-info").hide();
                $("#order").hide();
            }
        }
    });    
}

function set_address_data() {
    var $A = $("#receiverName");
    var $B = $("#receiverPhone");
    var $C = $("#receiverAddress");
    if (!$A||!$B||!$C) return false;

    $.ajax({
        type: "GET",
        url: SERVER_PATH + "/user/status?token=" + window.sessionStorage.getItem("token"),
        xhrFields: {withCredentials: true},
        success: function (result) {
            if (result.status == "0" && result.data) {
                $A.val(result.data.receiverName);
                $B.val(result.data.receiverPhone);
                $C.val(result.data.receiverAddress);
            } else {
            }
        }
    });    
}

function address() {
    window.location.href = "./address.html" 
}
function logout() {
    $.ajax({
        type: "GET",
        url: SERVER_PATH + "/user/logout?token=" + window.sessionStorage.getItem("token"),
        xhrFields: {withCredentials: true},
        success: function (result) {
            if (result.status) {
                alertBox(result.data.message);
                return false;
            }
            alertBox("已经注销！", function(){
                window.location.href = "./login.html"
            });
        }
    });    
}
function edit_address() {
    var receiverName = $("#receiverName").val();
    var receiverPhone = $("#receiverPhone").val();
    var receiverAddress = $("#receiverAddress").val();

    if (!receiverName) {
        alertBox("收货人不能为空！");
        return false;
    }
    if (!receiverPhone) {
        alertBox("收货电话不能为空！");
        return false;
    }
    if (!receiverAddress) {
        alertBox("收货地址不能为空！");
        return false;
    }

    $.ajax({
        type: "POST",
        url: SERVER_PATH + "/user/edit/address?token=" + window.sessionStorage.getItem("token"),
        data: {
            "receiverName": receiverName,
            "receiverPhone": receiverPhone,
            "receiverAddress": receiverAddress
        },
        xhrFields: {withCredentials: true},
        success: function (result) {
            if (result.status) {
                alert(result.data.message);
                return false;
            }
            alertBox("修改成功！", function(){
                window.location.href = "seckill.html";
            });
        }
    });    
}