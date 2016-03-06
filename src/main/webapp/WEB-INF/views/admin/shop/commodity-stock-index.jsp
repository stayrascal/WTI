<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<div class="tabbable tabbable-primary">
    <ul class="nav nav-pills">
        <li class="active">
            <a class="tab-default" data-toggle="tab" href="#tab-auto">分批次库存管理</a>
        </li>
        <li>
            <a class="tab-default" data-toggle="tab"
               href="${base}/biz/stock/commodity-stock!forward?_to_=sumByStorageLocation">按库存地汇总库存量</a>
        </li>
        <li>
            <a class="tab-default" data-toggle="tab"
               href="${base}/biz/stock/commodity-stock!forward?_to_=sumByCommodity">按商品汇总库存量</a>
        </li>
        <li>
            <a class="tab-default" data-toggle="tab" href="${base}/biz/stock/stock-in-out">库存变更流水记录</a>
        </li>
        <li class="tools pull-right">
            <a class="btn default reload" href="javascript:"><i class="fa fa-refresh"></i></a>
        </li>
    </ul>
    <div class="tab-content">
        <div class="tab-pane fade active in">
            <div class="row search-form-default">
                <div class="col-md-12">
                    <form action="#" method="get" class="form-inline form-validation form-search form-search-init"
                          data-grid-search=".grid-biz-stock-commodity-stock">
                        <div class="input-group">
                            <div class="input-cont">
                                <input type="text" name="search['CN_commodity.barcode_OR_commodity.title']"
                                       class="form-control"
                                       placeholder="商品编码、名称...">
                            </div>
                            <span class="input-group-btn">
                                <button class="btn green" type="submmit">
                                    <i class="m-icon-swapright m-icon-white"></i>&nbsp; 查&nbsp;询
                                </button>
                                <button class="btn default hidden-inline-xs" type="reset">
                                    <i class="fa fa-undo"></i>&nbsp; 重&nbsp;置
                                </button>
                            </span>
                        </div>
                    </form>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12">
                    <table class="grid-biz-stock-commodity-stock"></table>
                </div>
            </div>
        </div>
    </div>
</div>
<%--
<script type="text/javascript">
    $(function () {
        $(".grid-biz-stock-commodity-stock").data("gridOptions", {
            url : WEB_ROOT + '/admin/commodity/stock/list',
            colModel : [ {
                label : '商品主键',
                name : 'commodity.id',
                hidden : true,
                hidedlg : true,
                editable : true
            }, {
                label : '实物条码',
                name : 'commodity.barcode',
                hidden : true,
                width : 100,
                align : 'center',
                responsive : 'sm'
            }, {
                label : '商品编码',
                name : 'commodity.sku',
                width : 80,
                editable : true,
                editrules : {
                    required : true
                },
                editoptions : {
                    updatable : false,
                    dataInit : function(elem) {
                        var $grid = $(this);
                        var $elem = $(elem);
                        $elem.dblclick(function() {
                            $(this).popupDialog({
                                url : WEB_ROOT + '/biz/md/commodity!forward?_to_=selection',
                                title : '选取商品',
                                callback : function(item) {
                                    var $curRow = $elem.closest("tr.jqgrow");
                                    $grid.jqGrid("setEditingRowdata", {
                                        'commodity.id' : item.id,
                                        'commodity.sku' : item.sku,
                                        'commodity.barcode' : item.barcode,
                                        'commodity.title' : item.title
                                    }, true);
                                }
                            })
                        }).keypress(function(event) {
                            return false;
                        });
                    }
                },
                align : 'center'
            }, {
                label : '商品名称',
                name : 'commodity.title',
                editable : true,
                editoptions : {
                    updatable : false,
                    dataInit : function(elem) {
                        var $grid = $(this);
                        var $elem = $(elem);
                        $elem.dblclick(function() {
                            $elem.closest(".jqgrow").find("input[name='commodity.barcode']").dblclick();
                        }).keypress(function(event) {
                            return false;
                        });
                    }
                },
                width : 150,
                editrules : {
                    required : true
                },
                align : 'left'
            }, {
                label : '库存地',
                name : 'storageLocation.id',
                editable : true,
                width : 150,
                editrules : {
                    required : true
                },
                stype : 'select',
                editoptions : {
                    updatable : false,
                    value : Biz.getStockDatas()
                },
                align : 'left'
            }, {
                label : '批次号',
                name : 'batchNo',
                width : 100,
                editable : true,
                align : 'center'
            }, {
                label : '生产日期',
                name : 'productDate',
                width : 80,
                hidden : true,
                editable : true,
                formatter : 'date'
            }, {
                label : '到期日期',
                name : 'expireDate',
                width : 80,
                editable : true,
                formatter : 'date'
            }, {
                label : '当前成本价',
                name : 'costPrice',
                editable : true,
                width : 60,
                formatter : 'currency',
                editoptions : {
                    dataInit : function(elem) {
                        var $grid = $(this);
                        var $elem = $(elem);
                        var rowid = $elem.closest("tr.jqgrow").attr("id");
                        var rowdata = $grid.jqGrid("getRowData", rowid);
                        if ($elem.val() == '') {
                            $elem.val(0);
                        }
                    }
                }
            }, {
                label : '当前库存量',
                name : 'curStockQuantity',
                width : 60,
                editable : true,
                formatter : 'number',
                editoptions : {
                    dataInit : function(elem) {
                        var $grid = $(this);
                        var $elem = $(elem);
                        var rowid = $elem.closest("tr.jqgrow").attr("id");
                        var rowdata = $grid.jqGrid("getRowData", rowid);
                        if ($elem.val() == '') {
                            $elem.val(0);
                        }
                    }
                }
            }, {
                label : '销售锁定库存',
                name : 'salingTotalQuantity',
                width : 60,
                editable : true,
                formatter : 'number',
                editoptions : {
                    dataInit : function(elem) {
                        var $grid = $(this);
                        var $elem = $(elem);
                        var rowid = $elem.closest("tr.jqgrow").attr("id");
                        var rowdata = $grid.jqGrid("getRowData", rowid);
                        if ($elem.val() == '') {
                            $elem.val(0);
                        }
                    }
                }
            }, {
                label : '采购在途库存',
                name : 'purchasingTotalQuantity',
                width : 60,
                editable : true,
                formatter : 'number',
                editoptions : {
                    dataInit : function(elem) {
                        var $grid = $(this);
                        var $elem = $(elem);
                        var rowid = $elem.closest("tr.jqgrow").attr("id");
                        var rowdata = $grid.jqGrid("getRowData", rowid);
                        if ($elem.val() == '') {
                            $elem.val(0);
                        }
                    }
                }
            }, {
                label : '库存报警阀值',
                name : 'stockThresholdQuantity',
                editable : true,
                width : 60,
                formatter : 'number',
                editoptions : {
                    dataInit : function(elem) {
                        var $grid = $(this);
                        var $elem = $(elem);
                        var rowid = $elem.closest("tr.jqgrow").attr("id");
                        var rowdata = $grid.jqGrid("getRowData", rowid);
                        if ($elem.val() == '') {
                            $elem.val(0);
                        }
                    }
                }
            }, {
                label : '计算可用库存',
                name : 'availableQuantity',
                width : 60,
                formatter : 'number'
            } ],
            postData : {
                "search['FETCH_storageLocation']" : "INNER"
            },
            editurl : WEB_ROOT + '/biz/stock/commodity-stock!doSave',
            delurl : WEB_ROOT + '/biz/stock/commodity-stock!doDelete',

            subGrid : true,
            subGridRowExpanded : function(subgrid_id, row_id) {
                Grid.initSubGrid(subgrid_id, row_id, {
                    url : WEB_ROOT + '/biz/stock/stock-in-out!findByPage',
                    postData : {
                        "search['EQ_commodityStock.id']" : row_id,
                        "search['FETCH_commodityStock.storageLocation']" : "INNER.INNER"
                    },
                    colModel : [ {
                        label : '时间',
                        name : 'createdDate',
                        stype : 'date',
                        align : 'center'
                    }, {
                        label : '实物量',
                        name : 'quantity',
                        width : 60,
                        formatter : 'number',
                        tooltips : '本次结余量(之前结余量-本次变更量)',
                        formatter : function(cellValue, options, rowdata, action) {
                            if (rowdata.diffQuantity < 0) {
                                return rowdata.quantity + "(" + rowdata.originalQuantity + "-" + (-rowdata.diffQuantity) + ")";
                            } else {
                                return rowdata.quantity + "(" + rowdata.originalQuantity + "+" + (rowdata.diffQuantity) + ")";
                            }
                        },
                        align : 'right'
                    }, {
                        label : '锁定量',
                        name : 'originalSalingQuantity',
                        width : 60,
                        formatter : 'number',
                        tooltips : '本次结余量(之前结余量-本次变更量)',
                        formatter : function(cellValue, options, rowdata, action) {
                            if (rowdata.diffSalingQuantity < 0) {
                                return rowdata.salingQuantity + "(" + rowdata.originalSalingQuantity + "-" + (-rowdata.diffSalingQuantity) + ")";
                            } else {
                                return rowdata.salingQuantity + "(" + rowdata.originalSalingQuantity + "+" + (rowdata.diffSalingQuantity) + ")";
                            }
                        },
                        align : 'right'
                    }, {
                        label : '在途量',
                        name : 'originalPurchasingQuantity',
                        width : 60,
                        formatter : 'number',
                        tooltips : '本次结余量(之前结余量-本次变更量)',
                        formatter : function(cellValue, options, rowdata, action) {
                            if (rowdata.diffPurchasingQuantity < 0) {
                                return rowdata.purchasingQuantity + "(" + rowdata.originalPurchasingQuantity + "-" + (-rowdata.diffPurchasingQuantity) + ")";
                            } else {
                                return rowdata.purchasingQuantity + "(" + rowdata.originalPurchasingQuantity + "+" + (rowdata.diffPurchasingQuantity) + ")";
                            }
                        },
                        align : 'right'
                    }, {
                        label : '凭证号',
                        name : 'voucher',
                        width : 100,
                        align : 'center'
                    }, {
                        label : '类型',
                        name : 'voucherType',
                        width : 80,
                        align : 'center',
                        stype : 'select',
                        searchoptions : {
                            value : Util.getCacheEnumsByType('voucherTypeEnum')
                        }
                    }, {
                        label : '操作人',
                        name : 'createdBy',
                        align : 'center',
                        width : 50
                    }, {
                        label : '操作摘要',
                        name : 'operationSummary',
                        width : 150
                    } ],
                    sortorder : "desc",
                    sortname : "createdDate",
                    multiselect : false
                })
            }
        });
    });
</script>
--%>
</body>
</html>

