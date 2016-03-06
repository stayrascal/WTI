$.fn.fmatter.showlink = function (c, a, l) {
    var e = {
        baseLinkUrl: a.baseLinkUrl,
        showAction: a.showAction,
        addParam: a.addParam || "",
        target: a.target,
        idName: a.idName
    }, f = "", g = "", h;
    if (a.colModel !== undefined && a.colModel.formatoptions !== undefined) {
        e = $.extend({}, e, a.colModel.formatoptions)
    }
    if (e.target) {
        f = ' target="' + e.target + '"'
    }
    if (e.title) {
        g = ' title="' + e.title + '"'
    }
    var b = null;
    if (e.idValue == "id") {
        b = a.rowId
    } else {
        b = l[e.idValue];
        if ((b == undefined || b == "") && e.idValue.indexOf(".") > -1) {
            var d = l;
            $.each(e.idValue.split("."), function (m, n) {
                d = d[n]
            });
            b = d
        }
    }
    h = e.baseLinkUrl + e.showAction + "?" + e.idName + "=" + b + e.addParam;
    if ($.fmatter.isString(c) || $.fmatter.isNumber(c)) {
        return "<a " + f + g + ' href="' + h + '">' + c + "</a>"
    }
    return $.fn.fmatter.defaultFormat(c, a)
};
var Grid = function () {
    var a = false;
    return {
        initGridDefault: function () {
            $.extend($.ui.multiselect, {locale: {addAll: "全部添加", removeAll: "全部移除", itemsCount: "已选择项目列表"}});
            $.extend($.jgrid.ajaxOptions, {dataType: "json"});
            $.extend($.jgrid.defaults, {
                datatype: "json",
                loadui: false,
                loadonce: false,
                filterToolbar: {},
                ignoreCase: true,
                prmNames: {npage: "npage"},
                jsonReader: {repeatitems: false, root: "content", total: "totalPages", records: "totalElements"},
                treeReader: {
                    level_field: "extraAttributes.level",
                    parent_id_field: "extraAttributes.parent",
                    leaf_field: "extraAttributes.isLeaf",
                    expanded_field: "extraAttributes.expanded",
                    loaded: "extraAttributes.loaded",
                    icon_field: "extraAttributes.icon"
                },
                autowidth: true,
                rowNum: 15,
                page: 1,
                altclass: "ui-jqgrid-evennumber",
                height: "stretch",
                viewsortcols: [true, "vertical", true],
                mtype: "GET",
                viewrecords: true,
                rownumbers: true,
                toppager: true,
                recordpos: "left",
                gridview: true,
                altRows: true,
                sortable: false,
                multiboxonly: true,
                multiselect: true,
                multiSort: false,
                forceFit: false,
                shrinkToFit: true,
                sortorder: "desc",
                sortname: "id",
                ajaxSelectOptions: {cache: true},
                loadError: function (g, l, c, f) {
                    try {
                        var d = "表格数据加载处理失败,请尝试刷新或联系管理员";
                        var b = jQuery.parseJSON(g.responseText);
                        if (b.type == "failure" || b.type == "error") {
                            d = b.message
                        }
                        Global.notify("error", d)
                    } catch (h) {
                        Global.notify("error", "数据处理异常")
                    }
                    App.unblockUI()
                },
                subGridOptions: {reloadOnExpand: false}
            });
            $.extend($.jgrid.search, {
                multipleSearch: true,
                multipleGroup: true,
                width: 700,
                jqModal: false,
                searchOperators: true,
                stringResult: true,
                searchOnEnter: true,
                defaultSearch: "bw",
                operandTitle: "点击选择查询方式",
                odata: [{oper: "eq", text: "等于\u3000\u3000"}, {oper: "ne", text: "不等\u3000\u3000"}, {
                    oper: "lt",
                    text: "小于\u3000\u3000"
                }, {oper: "le", text: "小于等于"}, {oper: "gt", text: "大于\u3000\u3000"}, {
                    oper: "ge",
                    text: "大于等于"
                }, {oper: "bw", text: "开始于"}, {oper: "bn", text: "不开始于"}, {oper: "in", text: "属于\u3000\u3000"}, {
                    oper: "ni",
                    text: "不属于"
                }, {oper: "ew", text: "结束于"}, {oper: "en", text: "不结束于"}, {oper: "cn", text: "包含\u3000\u3000"}, {
                    oper: "nc",
                    text: "不包含"
                }, {oper: "nu", text: "不存在"}, {oper: "nn", text: "存在"}, {oper: "bt", text: "介于"}],
                operands: {
                    eq: "=",
                    ne: "!",
                    lt: "<",
                    le: "<=",
                    gt: ">",
                    ge: ">=",
                    bw: "^",
                    bn: "!^",
                    "in": "=",
                    ni: "!=",
                    ew: "|",
                    en: "!@",
                    cn: "~",
                    nc: "!~",
                    nu: "#",
                    nn: "!#",
                    bt: "~~"
                }
            });
            $.extend($.jgrid.del, {
                serializeDelData: function (b) {
                    b.ids = b.id;
                    b.id = "";
                    return b
                }, errorTextFormat: function (c) {
                    var b = jQuery.parseJSON(c.responseText);
                    return b.message
                }, afterComplete: function (d) {
                    var c = [];
                    var b = jQuery.parseJSON(d.responseText);
                    if (b.type == "success") {
                        top.$.publishMessage(b.message);
                        c[0] = true
                    } else {
                        top.$.publishError(b.message);
                        c[0] = false
                    }
                    return c
                }, ajaxDelOptions: {dataType: "json"}
            });
            $.jgrid.extend({
                bindKeys: function (b) {
                    var c = $.extend({
                        upKey: true,
                        downKey: true,
                        onEnter: null,
                        onSpace: null,
                        onLeftKey: null,
                        onRightKey: null,
                        scrollingRows: true
                    }, b || {});
                    return this.each(function () {
                        var d = this;
                        if (!$("body").is("[role]")) {
                            $("body").attr("role", "application")
                        }
                        d.p.scrollrows = c.scrollingRows;
                        $(d).keydown(function (h) {
                            var l = $(d).find("tr[tabindex=0]")[0], n, g, m, f = d.p.treeReader.expanded_field;
                            if (l) {
                                m = d.p._index[$.jgrid.stripPref(d.p.idPrefix, l.id)];
                                if (h.keyCode === 37 || h.keyCode === 38 || h.keyCode === 39 || h.keyCode === 40) {
                                    if (h.keyCode === 38 && c.upKey) {
                                        g = l.previousSibling;
                                        n = "";
                                        if (g) {
                                            if ($(g).is(":hidden")) {
                                                while (g) {
                                                    g = g.previousSibling;
                                                    if (!$(g).is(":hidden") && $(g).hasClass("jqgrow")) {
                                                        n = g.id;
                                                        break
                                                    }
                                                }
                                            } else {
                                                n = g.id
                                            }
                                        }
                                        $(d).jqGrid("setSelection", n, true, h);
                                        h.preventDefault()
                                    }
                                    if (h.keyCode === 40 && c.downKey) {
                                        g = l.nextSibling;
                                        n = "";
                                        if (g) {
                                            if ($(g).is(":hidden")) {
                                                while (g) {
                                                    g = g.nextSibling;
                                                    if (!$(g).is(":hidden") && $(g).hasClass("jqgrow")) {
                                                        n = g.id;
                                                        break
                                                    }
                                                }
                                            } else {
                                                n = g.id
                                            }
                                        }
                                        $(d).jqGrid("setSelection", n, true, h);
                                        h.preventDefault()
                                    }
                                    if (h.keyCode === 37) {
                                        if (d.p.treeGrid && d.p.data[m][f]) {
                                            $(l).find("div.treeclick").trigger("click")
                                        }
                                        $(d).triggerHandler("jqGridKeyLeft", [d.p.selrow]);
                                        if ($.isFunction(c.onLeftKey)) {
                                            c.onLeftKey.call(d, d.p.selrow)
                                        }
                                    }
                                    if (h.keyCode === 39) {
                                        if (d.p.treeGrid && !d.p.data[m][f]) {
                                            $(l).find("div.treeclick").trigger("click")
                                        }
                                        $(d).triggerHandler("jqGridKeyRight", [d.p.selrow]);
                                        if ($.isFunction(c.onRightKey)) {
                                            c.onRightKey.call(d, d.p.selrow)
                                        }
                                    }
                                } else {
                                    if (h.keyCode === 13) {
                                        var e = h.target;
                                        if (e.tagName === "TEXTAREA") {
                                            return true
                                        }
                                        h.stopPropagation();
                                        $(d).triggerHandler("jqGridKeyEnter", [d.p.selrow]);
                                        if ($.isFunction(c.onEnter)) {
                                            c.onEnter.call(d, d.p.selrow)
                                        }
                                    } else {
                                        if (h.keyCode === 32) {
                                            h.stopPropagation();
                                            $(d).triggerHandler("jqGridKeySpace", [d.p.selrow]);
                                            if ($.isFunction(c.onSpace)) {
                                                c.onSpace.call(d, d.p.selrow)
                                            }
                                        } else {
                                            if (h.keyCode === 27) {
                                                h.stopPropagation();
                                                $(d).jqGrid("restoreRow", d.p.selrow, c.afterrestorefunc);
                                                $(d).jqGrid("showAddEditButtons")
                                            }
                                        }
                                    }
                                }
                            }
                        })
                    })
                }, refresh: function () {
                    this.each(function () {
                        var b = this;
                        if (!b.grid) {
                            return
                        }
                        $(b).jqGrid("setGridParam", {datatype: "json"}).trigger("reloadGrid")
                    })
                }, search: function (b) {
                    this.each(function () {
                        var e = this;
                        if (!e.grid) {
                            return
                        }
                        var c = $(e).jqGrid("getGridParam", "url");
                        for (var d in b) {
                            c = AddOrReplaceUrlParameter(c, d, b[d])
                        }
                        $(e).jqGrid("setGridParam", {url: c, page: 1}).trigger("reloadGrid")
                    })
                }, exportExcelLocal: function (b) {
                    this.each(function () {
                        var f = this;
                        if (!f.grid) {
                            return
                        }
                        if (!confirm("确认导出当前页面 " + f.p.caption + " 数据为Excel下载文件？")) {
                            return
                        }
                        var e = [];
                        e = $(f).getDataIDs();
                        var m = f.p.colModel;
                        var p = f.p.colNames;
                        var h = "";
                        for (k = 0; k < p.length; k++) {
                            var n = m[k];
                            if (n.hidedlg || n.hidden || n.disableExport) {
                                continue
                            }
                            h = h + p[k] + "\t"
                        }
                        h = h + "\n";
                        for (i = 0; i < e.length; i++) {
                            data = $(f).getRowData(e[i]);
                            for (j = 0; j < p.length; j++) {
                                var n = m[j];
                                if (n.hidedlg || n.hidden || n.disableExport) {
                                    continue
                                }
                                var g = data[n.name];
                                var l = null;
                                if (n.searchoptions && n.searchoptions.value) {
                                    l = n.searchoptions.value
                                } else {
                                    if (n.editoptions && n.editoptions.value) {
                                        l = n.editoptions.value
                                    }
                                }
                                if (l) {
                                    g = l[g]
                                }
                                if (g.indexOf("<") > -1 && g.indexOf(">") > -1) {
                                    g = $(g).text()
                                }
                                if (g == "") {
                                    g = data[n.name]
                                }
                                if (g == "null" || g == null) {
                                    g = ""
                                }
                                g = g.replace(/\&nbsp;/g, "");
                                h = h + g + "\t"
                            }
                            h = h + "\n"
                        }
                        h = h + "\n";
                        var c = $('<form method="post" target = "_blank" action="' + WEB_ROOT + '/admin/util/grid/export"></form>').appendTo($("body"));
                        var o = $('<input type="hidden" name="exportDatas"/>').appendTo(c);
                        var d = $('<input type="hidden" name="fileName"/>').appendTo(c);
                        d.val("export-data.xls");
                        o.val(h);
                        c.submit();
                        c.remove()
                    })
                }, refreshRowIndex: function () {
                    var b = $(this);
                    $.each($(b).jqGrid("getDataIDs"), function (c, d) {
                        $(b).find("#" + d).find("input,select").each(function () {
                            var e = $(this).attr("name");
                            $(this).attr("name", e.substring(0, e.indexOf("[") + 1) + c + e.substring(e.indexOf("]"), e.length))
                        })
                    })
                }, getAtLeastOneSelectedItem: function (g) {
                    var f = $(this);
                    var d = [];
                    var e = jQuery(f).jqGrid("getGridParam", "selarrrow");
                    if (e.length > 0) {
                        for (var b = 0; b < e.length; b++) {
                            var c = $("#jqg_" + jQuery(f).attr("id") + "_" + e[b]).is(":disabled");
                            if (!c) {
                                d.push(e[b])
                            }
                        }
                    } else {
                        var h = jQuery(f).jqGrid("getGridParam", "selrow");
                        if (h) {
                            d.push(h)
                        }
                    }
                    if (g) {
                        jQuery(f).find("table.jqsubgrid").each(function () {
                            var n = $(this).jqGrid("getGridParam", "selarrrow");
                            for (var l = 0; l < n.length; l++) {
                                var m = $("#jqg_" + jQuery(this).attr("id") + "_" + e[l]).is(":disabled");
                                if (!m) {
                                    d.push(n[l])
                                }
                            }
                        })
                    }
                    if (d.length == 0) {
                        Global.notify("error", "请至少选择一条行项目！");
                        return false
                    } else {
                        return d
                    }
                }, getOnlyOneSelectedItem: function (e) {
                    var g = $(this);
                    var d = [];
                    var f = jQuery(g).jqGrid("getGridParam", "selarrrow");
                    if (f.length > 0) {
                        for (var b = 0; b < f.length; b++) {
                            var c = $("#jqg_" + jQuery(g).attr("id") + "_" + f[b]).is(":disabled");
                            if (!c) {
                                d.push(f[b])
                            }
                        }
                    } else {
                        var h = jQuery(g).jqGrid("getGridParam", "selrow");
                        if (h) {
                            d.push(h)
                        }
                    }
                    if (d.length == 0) {
                        if (e) {
                            Global.notify("error", "请选取操作项目")
                        }
                        return false
                    } else {
                        if (d.length > 1) {
                            Global.notify("error", "只能选择一条操作项目");
                            return false
                        }
                        return d[0]
                    }
                }, getSelectedRowdatas: function () {
                    var b = $(this);
                    var c = [];
                    var e = b.jqGrid("getGridParam", "selarrrow");
                    if (e) {
                        $.each(e, function (g, h) {
                            var l = b.jqGrid("getRowData", h);
                            l.id = h;
                            c.push(l)
                        })
                    } else {
                        var d = b.jqGrid("getGridParam", "selrow");
                        if (d) {
                            var f = b.jqGrid("getRowData", d);
                            f.id = d;
                            c.push(f)
                        }
                    }
                    return c
                }, getSelectedRowdata: function () {
                    var b = $(this);
                    var c = b.jqGrid("getGridParam", "selrow");
                    if (c) {
                        return b.jqGrid("getRowData", c)
                    }
                }, getDirtyRowdatas: function () {
                    var l = $(this);
                    var g = [];
                    var f = l.jqGrid("getGridParam", "colModel");
                    var d = [];
                    $.each(f, function (o, n) {
                        if (n.editable) {
                            d.push(n.name)
                        }
                    });
                    var b = l.jqGrid("getDataIDs");
                    var h = 0;
                    $.each(b, function (n, o) {
                        if (!Util.startWith(o, "-") && o != "") {
                            h++
                        }
                    });
                    var e = BooleanUtil.toBoolean(l.attr("data-clone"));
                    $.each(b, function (n, q) {
                        var p = l.jqGrid("getRowData", q);
                        if (BooleanUtil.toBoolean(p["extraAttributes.dirtyRow"])) {
                            if (Util.startWith(q, "-")) {
                                q = ""
                            }
                            var o = {id: q};
                            $.each(d, function (s, r) {
                                o[r] = p[r]
                            });
                            o._arrayIndex = p._arrayIndex;
                            if (p["extraAttributes.operation"]) {
                                o["extraAttributes.operation"] = p["extraAttributes.operation"]
                            }
                            g.push(o)
                        }
                    });
                    var m = l.jqGrid("getGridParam", "batchEntitiesPrefix");
                    if (m) {
                        var c = {};
                        $.each(g, function (n, p) {
                            var o = p._arrayIndex;
                            delete p._arrayIndex;
                            if (o == undefined || o == "") {
                                o = h;
                                h++
                            }
                            $.each(p, function (r, q) {
                                c[m + "[" + o + "]." + r] = q
                            })
                        });
                        return c
                    }
                    return g
                }, insertNewRowdata: function (f) {
                    var b = $(this);
                    var e = null;
                    var d = b.jqGrid("getDataIDs");
                    $.each(d, function (g, l) {
                        var h = b.jqGrid("getRowData", l);
                        if (!BooleanUtil.toBoolean(h["extraAttributes.dirtyRow"])) {
                            e = l;
                            return false
                        }
                    });
                    var c = -Math.floor(new Date().getTime() + Math.random() * 100 + 100);
                    f["extraAttributes.dirtyRow"] = true;
                    if (e) {
                        b.jqGrid("addRowData", c, f, "before", e)
                    } else {
                        b.jqGrid("addRowData", c, f, "last")
                    }
                    return c
                }, setEditingRowdata: function (l, c) {
                    var b = $(this);
                    var d = b.find("tbody");
                    for (var f in l) {
                        var g = "input[name='" + f + "'],select[name='" + f + "'],textarea[name='" + f + "']";
                        var e = d.find(g);
                        if (c == false) {
                            if ($.trim(e.val()) != "") {
                                continue
                            }
                        }
                        var h = l[f];
                        e.val(h).attr("title", h);
                        if (e.is("select")) {
                            e.select2({
                                openOnEnter: false, placeholder: "请选择...", matcher: function (n, q) {
                                    var m = Pinyin.getCamelChars(q) + "";
                                    var p = m.toUpperCase().indexOf(n.toUpperCase()) == 0;
                                    var o = q.toUpperCase().indexOf(n.toUpperCase()) == 0;
                                    return (p || o)
                                }
                            })
                        }
                    }
                }, getEditingRowdata: function () {
                    var b = $(this);
                    var d = b.find("tbody");
                    var e = {};
                    var c = "input,select,textarea";
                    d.find(c).each(function () {
                        var f = $(this);
                        e[f.attr("name")] = f.val()
                    });
                    return e
                }, isEditingMode: function (d) {
                    var b = $(this);
                    var c = b.find('tr[editable="1"]');
                    if (c.size() > 0) {
                        if (d == undefined) {
                            return true
                        }
                        if (d === true) {
                            alert("请先保存或取消正在编辑的表格数据行项后再操作")
                        } else {
                            alert(d)
                        }
                        return true
                    }
                    return false
                }, sumColumn: function (d, f) {
                    var b = $(this);
                    if (f == undefined) {
                        f = 2
                    }
                    var e = b.jqGrid("getCol", d, false, "sum");
                    var c = Math.pow(10, f);
                    return Math.round(e * c) / c
                }, getDataFromBindSeachForm: function (d) {
                    var b = $(this);
                    var c = b.jqGrid("getGridParam", "bindSearchFormData");
                    var e = c[d];
                    return e
                }, inlineNav: function (b, c) {
                    c = $.extend(true, {
                        edit: true,
                        editicon: "ui-icon-pencil",
                        add: true,
                        addicon: "ui-icon-plus",
                        save: true,
                        saveicon: "ui-icon-disk",
                        cancel: true,
                        cancelicon: "ui-icon-cancel",
                        del: true,
                        delicon: "ui-icon-trash",
                        addParams: {addRowParams: {extraparam: {}}},
                        editParams: {},
                        restoreAfterSelect: true
                    }, $.jgrid.nav, c || {});
                    return this.each(function () {
                        if (!this.grid) {
                            return
                        }
                        var n = this, f, l = $.jgrid.jqID(n.p.id), e = $(n.p.toppager).attr("id");
                        n.p._inlinenav = true;
                        if (c.addParams.useFormatter === true) {
                            var d = n.p.colModel, h;
                            for (h = 0; h < d.length; h++) {
                                if (d[h].formatter && d[h].formatter === "actions") {
                                    if (d[h].formatoptions) {
                                        var m = {
                                            keys: false,
                                            onEdit: null,
                                            onSuccess: null,
                                            afterSave: null,
                                            onError: null,
                                            afterRestore: null,
                                            extraparam: {},
                                            url: null
                                        }, g = $.extend(m, d[h].formatoptions);
                                        c.addParams.addRowParams = {
                                            keys: g.keys,
                                            oneditfunc: g.onEdit,
                                            successfunc: g.onSuccess,
                                            url: g.url,
                                            extraparam: g.extraparam,
                                            aftersavefunc: g.afterSave,
                                            errorfunc: g.onError,
                                            afterrestorefunc: g.afterRestore
                                        }
                                    }
                                    break
                                }
                            }
                        }
                        $(n).jqGrid("navSeparatorAdd", b);
                        if (n.p.toppager) {
                            $(n).jqGrid("navSeparatorAdd", n.p.toppager)
                        }
                        if (c.add) {
                            $(n).jqGrid("navButtonAdd", b, {
                                caption: c.addtext,
                                title: c.addtitle,
                                buttonicon: c.addicon,
                                id: n.p.id + "_iladd",
                                onClickButton: function () {
                                    var q = $(n).getOnlyOneSelectedItem(false);
                                    if (q) {
                                        var r = $(n).getRowData(q);
                                        c.addParams.initdata = r;
                                        var o = n.p.colModel, p;
                                        for (p = 0; p < o.length; p++) {
                                            if (o[p].editcopy == false) {
                                                delete c.addParams.initdata[o[p].name]
                                            } else {
                                                if (o[p].editcopy == "append") {
                                                    c.addParams.initdata[o[p].name] = r[o[p].name] + "_COPY"
                                                }
                                            }
                                        }
                                        $(n).jqGrid("resetSelection")
                                    } else {
                                        c.addParams.initdata = {id: ""}
                                    }
                                    c.addParams.rowID = -(new Date().getTime());
                                    $(n).jqGrid("addRow", c.addParams);
                                    if (!c.addParams.useFormatter) {
                                        $("#" + l + "_ilsave").removeClass("ui-state-disabled");
                                        $("#" + l + "_ilcancel").removeClass("ui-state-disabled");
                                        $("#" + l + "_iladd").addClass("ui-state-disabled");
                                        $("#" + l + "_iledit").addClass("ui-state-disabled");
                                        $("#" + l + "_toppager_ilsave").removeClass("ui-state-disabled");
                                        $("#" + l + "_toppager_ilcancel").removeClass("ui-state-disabled");
                                        $("#" + l + "_toppager_iladd").addClass("ui-state-disabled");
                                        $("#" + l + "_toppager_iledit").addClass("ui-state-disabled")
                                    }
                                }
                            });
                            if (n.p.toppager) {
                                $(n).jqGrid("navButtonAdd", n.p.toppager, {
                                    caption: c.addtext,
                                    title: c.addtitle,
                                    buttonicon: c.addicon,
                                    id: e + "_iladd",
                                    onClickButton: function () {
                                        $(".ui-icon-plus", $(n.p.pager)).click()
                                    }
                                })
                            }
                        }
                        if (c.edit) {
                            $(n).jqGrid("navButtonAdd", b, {
                                caption: c.edittext,
                                title: c.edittitle,
                                buttonicon: c.editicon,
                                id: n.p.id + "_iledit",
                                onClickButton: function () {
                                    var o = $(n).getOnlyOneSelectedItem();
                                    if (o) {
                                        if ($("#" + o, $(n)).hasClass("not-editable-row")) {
                                            alert("提示：当前行项不可编辑");
                                            return
                                        }
                                        $(n).jqGrid("editRow", o, c.editParams);
                                        $("#" + l + "_ilsave").removeClass("ui-state-disabled");
                                        $("#" + l + "_ilcancel").removeClass("ui-state-disabled");
                                        $("#" + l + "_iladd").addClass("ui-state-disabled");
                                        $("#" + l + "_iledit").addClass("ui-state-disabled");
                                        $("#" + l + "_toppager_ilsave").removeClass("ui-state-disabled");
                                        $("#" + l + "_toppager_ilcancel").removeClass("ui-state-disabled");
                                        $("#" + l + "_toppager_iladd").addClass("ui-state-disabled");
                                        $("#" + l + "_toppager_iledit").addClass("ui-state-disabled")
                                    }
                                }
                            });
                            if (n.p.toppager) {
                                $(n).jqGrid("navButtonAdd", n.p.toppager, {
                                    caption: c.edittext,
                                    title: c.edittitle,
                                    buttonicon: c.editicon,
                                    id: e + "_iledit",
                                    onClickButton: function () {
                                        $(".ui-icon-pencil", $(n.p.pager)).click()
                                    }
                                })
                            }
                        }
                        if (c.save) {
                            $(n).jqGrid("navButtonAdd", b, {
                                caption: c.savetext || "",
                                title: c.savetitle || "保存编辑行项",
                                buttonicon: c.saveicon,
                                id: n.p.id + "_ilsave",
                                onClickButton: function () {
                                    var p = n.p.savedRow[0] ? n.p.savedRow[0].id : false;
                                    if (p) {
                                        var o = n.p.prmNames, r = o.oper, q = {};
                                        if ($("#" + $.jgrid.jqID(p), "#" + l).hasClass("jqgrid-new-row")) {
                                            c.addParams.addRowParams.extraparam[r] = o.addoper;
                                            q = c.addParams.addRowParams;
                                            q.extraparam.id = ""
                                        } else {
                                            if (!c.editParams.extraparam) {
                                                c.editParams.extraparam = {}
                                            }
                                            c.editParams.extraparam[r] = o.editoper;
                                            q = c.editParams
                                        }
                                        q.extraparam["extraAttributes.dirtyRow"] = true;
                                        if ($(n).jqGrid("saveRow", p, q)) {
                                            $(n).jqGrid("showAddEditButtons")
                                        }
                                    }
                                }
                            });
                            $("#" + l + "_ilsave").addClass("ui-state-disabled");
                            if (n.p.toppager) {
                                $(n).jqGrid("navButtonAdd", n.p.toppager, {
                                    caption: c.savetext || "",
                                    title: c.savetitle || "保存编辑行项",
                                    buttonicon: c.saveicon,
                                    id: e + "_ilsave",
                                    onClickButton: function () {
                                        $(".ui-icon-disk", $(n.p.pager)).click()
                                    }
                                });
                                $("#" + l + "_toppager_ilsave").addClass("ui-state-disabled")
                            }
                        }
                        if (c.cancel) {
                            $(n).jqGrid("navButtonAdd", b, {
                                caption: c.canceltext || "",
                                title: c.canceltitle || "放弃正在编辑行项",
                                buttonicon: c.cancelicon,
                                id: n.p.id + "_ilcancel",
                                onClickButton: function () {
                                    var p = n.p.savedRow[0] ? n.p.savedRow[0].id : false, o = {};
                                    if (p) {
                                        if ($("#" + $.jgrid.jqID(p), "#" + l).hasClass("jqgrid-new-row")) {
                                            o = c.addParams.addRowParams
                                        } else {
                                            o = c.editParams
                                        }
                                        $(n).jqGrid("restoreRow", p, o)
                                    }
                                    $(n).jqGrid("resetSelection");
                                    $(n).jqGrid("showAddEditButtons")
                                }
                            });
                            if (n.p.toppager) {
                                $(n).jqGrid("navButtonAdd", n.p.toppager, {
                                    caption: c.canceltext || "",
                                    title: c.canceltitle || "放弃正在编辑行项",
                                    buttonicon: c.cancelicon,
                                    id: e + "_ilcancel",
                                    onClickButton: function () {
                                        $(".ui-icon-cancel", $(n.p.pager)).click()
                                    }
                                })
                            }
                        }
                        if (c.del) {
                            $(n).jqGrid("navSeparatorAdd", b);
                            $(n).jqGrid("navButtonAdd", b, {
                                caption: c.deltext || "",
                                title: c.deltitle || "删除所选行项",
                                buttonicon: c.delicon,
                                id: n.p.id + "_ildel",
                                onClickButton: function () {
                                    if (!$(this).hasClass("ui-state-disabled")) {
                                        var q = $(n).getAtLeastOneSelectedItem();
                                        if (q) {
                                            $(n).jqGrid("restoreRow", q);
                                            if ($.isFunction(c.delfunc)) {
                                                c.delfunc.call(n, q)
                                            } else {
                                                if (n.p.delurl == undefined || n.p.delurl == "clientArray") {
                                                    $.each(q, function (s, v) {
                                                        if (Util.startWith(v, "-")) {
                                                            $(n).jqGrid("delRowData", v)
                                                        } else {
                                                            var t = $(n).find("#" + v);
                                                            var u = $(n).jqGrid("getRowData", v);
                                                            for (var r in u) {
                                                                if (r == "id" || Util.endWith(r, ".id") || r == "_arrayIndex") {
                                                                } else {
                                                                    if (r == "extraAttributes.dirtyRow") {
                                                                        u[r] = true
                                                                    } else {
                                                                        if (r == "extraAttributes.operation") {
                                                                            u[r] = "remove"
                                                                        } else {
                                                                            u[r] = ""
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            $(n).jqGrid("setRowData", v, u);
                                                            t.hide()
                                                        }
                                                        if (n.p.afterInlineDeleteRow) {
                                                            n.p.afterInlineDeleteRow.call($(n), v)
                                                        }
                                                    })
                                                } else {
                                                    var q = $(n).getAtLeastOneSelectedItem();
                                                    if (q) {
                                                        var o = "id";
                                                        if (n.p.multiselect) {
                                                            o = "ids"
                                                        }
                                                        var p = Util.AddOrReplaceUrlParameter(n.p.delurl, o, q.join(","));
                                                        $(n).ajaxPostURL({
                                                            url: p, success: function (r) {
                                                                $.each(q, function (s, u) {
                                                                    var u = $.trim(u);
                                                                    if (r.data && r.data[u]) {
                                                                        var t = $(n).find("tr.jqgrow[id='" + u + "']");
                                                                        var v = r.data[u];
                                                                        t.pulsate({color: "#bf1c56", repeat: 3})
                                                                    } else {
                                                                        $(n).jqGrid("delRowData", u)
                                                                    }
                                                                })
                                                            }, confirmMsg: "确认批量删除所选记录吗？"
                                                        })
                                                    }
                                                }
                                            }
                                            $(n).jqGrid("showAddEditButtons")
                                        } else {
                                            $.jgrid.viewModal("#" + alertIDs.themodal, {
                                                gbox: "#gbox_" + $.jgrid.jqID(n.p.id),
                                                jqm: true
                                            });
                                            $("#jqg_alrt").focus()
                                        }
                                    }
                                    return false
                                }
                            });
                            if (n.p.toppager) {
                                $(n).jqGrid("navSeparatorAdd", n.p.toppager);
                                $(n).jqGrid("navButtonAdd", n.p.toppager, {
                                    caption: c.deltext || "",
                                    title: c.deltitle || "删除所选行项",
                                    buttonicon: c.delicon,
                                    id: e + "_ildel",
                                    onClickButton: function () {
                                        $(".ui-icon-trash", $(n.p.pager)).click()
                                    }
                                })
                            }
                        }
                        if (c.restoreAfterSelect === true) {
                            if ($.isFunction(n.p.beforeSelectRow)) {
                                f = n.p.beforeSelectRow
                            } else {
                                f = false
                            }
                            n.p.beforeSelectRow = function (q, p) {
                                var o = true;
                                if (n.p.savedRow.length > 0 && n.p._inlinenav === true && (q !== n.p.selrow && n.p.selrow !== null)) {
                                    if (n.p.selrow === c.addParams.rowID) {
                                        $(n).jqGrid("delRowData", n.p.selrow)
                                    } else {
                                        $(n).jqGrid("restoreRow", n.p.selrow, c.editParams)
                                    }
                                    $(n).jqGrid("showAddEditButtons")
                                }
                                if (f) {
                                    o = f.call(n, q, p)
                                }
                                return o
                            }
                        }
                        $(n).jqGrid("showAddEditButtons")
                    })
                }, showAddEditButtons: function () {
                    return this.each(function () {
                        if (!this.grid) {
                            return
                        }
                        var b = $.jgrid.jqID(this.p.id);
                        $("#" + b + "_ilsave").addClass("ui-state-disabled");
                        $("#" + b + "_ilcancel").addClass("ui-state-disabled");
                        $("#" + b + "_iladd").removeClass("ui-state-disabled");
                        $("#" + b + "_iledit").removeClass("ui-state-disabled");
                        $("#" + b + "_toppager_ilsave").addClass("ui-state-disabled");
                        $("#" + b + "_toppager_ilcancel").addClass("ui-state-disabled");
                        $("#" + b + "_toppager_iladd").removeClass("ui-state-disabled");
                        $("#" + b + "_toppager_iledit").removeClass("ui-state-disabled")
                    })
                }
            });
            a = true
        }, initAjax: function (b) {
            if (b == undefined) {
                b = $("body")
            }
            $('table[data-grid="table"],table[data-grid="items"]', b).each(function () {
                Grid.initGrid($(this))
            })
        }, initGrid: function (X, G) {
            if (!a) {
                Grid.initGridDefault()
            }
            var ak = $(X);
            if (ak.hasClass("ui-jqgrid-btable")) {
                return
            }
            if (ak.attr("id") == undefined) {
                ak.attr("id", "grid_" + new Date().getTime())
            }
            if (G == undefined && ak.data("gridOptions") == undefined) {
                alert("Grid options undefined: class=" + ak.attr("class"));
                return
            }
            var y = $.extend(true, {}, ak.data("gridOptions"), G);
            var d = ak.attr("data-grid");
            var J = null;
            var ac = ak.attr("id") + "-context-menu-container";
            var Y = null;
            var f = (d == "items" ? false : true);
            var K = (d == "items" ? false : true);
            var N = $.extend(true, {}, $.jgrid.defaults, {
                formatter: {
                    integer: {defaultValue: ""},
                    number: {decimalSeparator: ".", thousandsSeparator: ",", decimalPlaces: 2, defaultValue: ""},
                    currency: {decimalSeparator: ".", thousandsSeparator: ",", decimalPlaces: 2, defaultValue: ""}
                },
                cmTemplate: {sortable: d == "items" ? false : true},
                viewsortcols: d == "items" ? [true, "vertical", false] : [true, "vertical", true],
                altRows: d == "items" ? false : true,
                hoverrows: d == "items" ? false : true,
                pgbuttons: d == "items" ? false : true,
                pginput: d == "items" ? false : true,
                rowList: d == "items" ? [] : [10, 15, 20, 50, 100, 200, 500, 1000, 2000],
                inlineNav: {
                    add: y.editurl || d == "items" ? true : false,
                    edit: y.editurl || d == "items" ? true : false,
                    del: y.delurl || d == "items" ? true : false,
                    restoreAfterSelect: d == "items" ? false : true,
                    addParams: {
                        addRowParams: {
                            extraparam: {}, restoreAfterError: false, beforeSaveRow: function (c) {
                                if (N.beforeInlineSaveRow) {
                                    N.beforeInlineSaveRow.call(ak, c)
                                }
                            }, aftersavefunc: function (ax, ay) {
                                if (N.editurl == "clientArray") {
                                    ak.jqGrid("resetSelection");
                                    if (N.afterInlineSaveRow) {
                                        N.afterInlineSaveRow.call(ak, ax)
                                    }
                                    setTimeout(function () {
                                        $("#" + J).find(".ui-pg-div span.ui-icon-plus").click()
                                    }, 200);
                                    return
                                }
                                var aw = jQuery.parseJSON(ay.responseText);
                                if (aw.type == "success" || aw.type == "warning") {
                                    Global.notify(aw.type, aw.message);
                                    var c = aw.data.id;
                                    ak.find("#" + ax).attr("id", c);
                                    ak.jqGrid("resetSelection");
                                    ak.jqGrid("setSelection", c);
                                    if (N.afterInlineSaveRow) {
                                        N.afterInlineSaveRow.call(ak, ax)
                                    }
                                    setTimeout(function () {
                                        $("#" + J).find(".ui-pg-div span.ui-icon-plus").click()
                                    }, 200)
                                } else {
                                    if (aw.type == "failure" || aw.type == "error") {
                                        Global.notify("error", aw.message)
                                    } else {
                                        Global.notify("error", "数据处理异常，请联系管理员")
                                    }
                                }
                            }, errorfunc: function (aw, ax) {
                                var c = jQuery.parseJSON(ax.responseText);
                                Global.notify("error", c.message)
                            }
                        }
                    },
                    editParams: {
                        restoreAfterError: false, beforeSaveRow: function (c) {
                            if (N.beforeInlineSaveRow) {
                                N.beforeInlineSaveRow.call(ak, c)
                            }
                        }, oneditfunc: function (az) {
                            var aw = ak.jqGrid("getGridParam", "iCol");
                            var c = ak.jqGrid("getGridParam", "colModel")[aw];
                            var ay = ak.find("tr#" + az);
                            var aA = ay.find("> td:eq(" + aw + ")");
                            var ax = aA.find("input:visible:first");
                            if (ax.size() > 0 && ax.attr("readonly") == undefined) {
                                setTimeout(function () {
                                    ax.focus()
                                }, 200)
                            } else {
                                ay.find("input:visible:enabled:first").focus()
                            }
                        }, aftersavefunc: function (aw, az) {
                            var ay = true;
                            if (N.editurl != "clientArray") {
                                var c = jQuery.parseJSON(az.responseText);
                                if (c.type == "success" || c.type == "warning") {
                                    Global.notify(c.type, c.message)
                                } else {
                                    if (c.type == "failure" || c.type == "error") {
                                        Global.notify("error", c.message);
                                        ay = false
                                    } else {
                                        Global.notify("error", "数据处理异常，请联系管理员");
                                        ay = false
                                    }
                                }
                            }
                            if (ay) {
                                if (N.afterInlineSaveRow) {
                                    N.afterInlineSaveRow.call(ak, aw)
                                }
                                if (N.editurl != "clientArray") {
                                    var ax = ak.find("tr.jqgrow[id='" + aw + "']").next("tr");
                                    if (ax.size() > 0) {
                                        var aA = ax.attr("id");
                                        ak.jqGrid("resetSelection");
                                        ak.jqGrid("setSelection", aA);
                                        setTimeout(function () {
                                            $("#" + J).find(".ui-pg-div span.ui-icon-pencil").click()
                                        }, 200)
                                    }
                                }
                            }
                        }, errorfunc: function (aw, ax) {
                            if (ax.status == 404) {
                                Global.notify("error", ax.status + ": 请求地址未找到")
                            } else {
                                var c = jQuery.parseJSON(ax.responseText);
                                Global.notify("error", c.message)
                            }
                        }
                    }
                },
                filterToolbar: K,
                multiselect: f,
                contextMenu: true,
                columnChooser: true,
                exportExcelLocal: true,
                loadBeforeSend: function () {
                    App.blockUI(ak.closest(".ui-jqgrid"))
                },
                subGridBeforeExpand: function () {
                    var c = ak.closest(".ui-jqgrid-bdiv");
                    c.css({height: "auto"})
                },
                beforeProcessing: function (aw) {
                    if (aw && aw.content) {
                        var c = 1000;
                        $.each(aw.content, function (ax, ay) {
                            if (ay.extraAttributes && ay.extraAttributes.dirtyRow) {
                                ay.id = -(c++)
                            }
                        });
                        if (aw.totalElements >= (2147473647 - 10000)) {
                            ak.jqGrid("setGridParam", {recordtext: "{0} - {1}\u3000"})
                        }
                    }
                },
                loadComplete: function (ax) {
                    App.unblockUI(ak.closest(".ui-jqgrid"));
                    ak.jqGrid("showAddEditButtons");
                    if (ax == undefined) {
                        return
                    }
                    if (ax.total == undefined && ax.totalElements == undefined) {
                        alert("表格数据格式不正确");
                        return
                    }
                    if (ax && ax.content) {
                        $.each(ax.content, function (ay, az) {
                            ak.setRowData(az.id, {_arrayIndex: ay})
                        });
                        if (ax.totalElements >= (2147473647 - 10000)) {
                            ak.closest(".ui-jqgrid").find(".ui-pg-table td[id^='last_']").addClass("ui-state-disabled");
                            ak.closest(".ui-jqgrid").find(".ui-pg-table .ui-pg-input").each(function () {
                                $(this).parent().html($(this))
                            })
                        }
                    }
                    if (d == "items" && N.inlineNav.add != false) {
                        for (var aw = 1; aw <= 3; aw++) {
                            ak.addRowData(-aw, {})
                        }
                    }
                    if (ab == "enable" && N.contextMenu && Y.find("li").length > 0) {
                        ak.find("tr.jqgrow").each(function () {
                            $(this).contextmenu({
                                target: "#" + ac, onItem: function (aA, az) {
                                    var ay = $(az).attr("role-idx");
                                    Y.find('a[role-idx="' + ay + '"]').click();
                                    return true
                                }
                            })
                        })
                    }
                    if (N.footerrow) {
                        if (N.footerLocalDataColumn) {
                            $.each(N.footerLocalDataColumn, function (az, aB) {
                                var aA = ak.jqGrid("sumColumn", aB);
                                var ay = [];
                                ay[aB] = aA;
                                ak.footerData("set", ay)
                            })
                        } else {
                            $.each(N.colModel, function (aA, az) {
                                if (az.footersum != undefined && az.footersum == false) {
                                    return
                                }
                                if (az.formatter == "integer" || az.formatter == "currency" || az.formatter == "number") {
                                    var aB = ak.jqGrid("sumColumn", az.name);
                                    var ay = [];
                                    ay[az.name] = aB;
                                    ak.footerData("set", ay)
                                }
                            })
                        }
                    }
                    if (ak.attr("data-selected")) {
                        ak.jqGrid("setSelection", ak.attr("data-selected"), false)
                    }
                    var c = y.userLoadComplete;
                    if (c) {
                        c.call(ak, ax)
                    }
                    $('[data-hover="dropdown"]', ak.closest(".ui-jqgrid")).dropdownHover()
                },
                beforeSelectRow: function (ax) {
                    if (N.inlineNav.restoreAfterSelect == false) {
                        var aw = ak.jqGrid("getGridParam", "selrow");
                        var c = ak.find("tr#" + aw).attr("editable");
                        if (aw && aw != ax && c == "1") {
                            $("#" + J).find(".ui-pg-div span.ui-icon-disk").click();
                            return false
                        }
                    }
                    return true
                },
                onSelectRow: function (ax, c, aw) {
                    ak.find("tr.jqgrow").attr("tabindex", -1);
                    ak.find("tr.jqgrow[id='" + ax + "']").attr("tabindex", 0);
                    if (d == "items") {
                        $("#" + J).find(".ui-pg-div span.ui-icon-pencil").click()
                    }
                },
                onCellSelect: function (aw, c) {
                    ak.jqGrid("setGridParam", {iCol: c})
                },
                ondblClickRow: function (ay, aA, aw, az) {
                    var c = $("#" + J).find("i.fa-edit").parent("a");
                    if (c.size() > 0) {
                        c.click()
                    } else {
                        if (d != "items") {
                            var ax = $("#" + J).find(".ui-pg-div span.ui-icon-pencil");
                            if (ax.size() > 0) {
                                ax.click()
                            } else {
                                $("#" + J).find("i.fa-credit-card").parent("a").click()
                            }
                        }
                    }
                    az.stopPropagation()
                },
                editcol: "display"
            }, y);
            if ($.isFunction(N.url)) {
                N.url = N.url.call(ak)
            }
            if (N.url == undefined) {
                N.url = ak.attr("data-url")
            }
            if (N.url == undefined) {
                N.datatype = "local"
            }
            if (BooleanUtil.toBoolean(ak.attr("data-readonly"))) {
                N.inlineNav.add = false;
                N.inlineNav.edit = false;
                N.inlineNav.del = false
            }
            if (N.pager == undefined || N.pager) {
                J = ak.attr("id") + "_pager";
                $("<div id='" + J + "'/>").insertAfter(ak);
                N.pager = "#" + J
            } else {
                N.toppager = false
            }
            if (N.toppager) {
                N.toppager = "#" + ak.attr("id") + "_toppager"
            }
            if (N.treeGrid) {
                N.rownumbers = false
            }
            if (y.editurl == undefined && d == "items") {
                N.editurl = "clientArray"
            }
            if (y.delurl == undefined && d == "items") {
                N.delurl = "clientArray"
            }
            if (N.editurl == "clientArray") {
                N.cellsubmit = N.editurl
            } else {
                N.cellurl = N.editurl
            }
            var U = 0;
            var T = false;
            var V = [];
            var n = [];
            $.each(N.colModel, function (aw, c) {
                n.push(c.name)
            });
            if ($.inArray("id", n)) {
                N.colModel.push({label: "流水号", name: "id", width: 50, hidden: true});
                if (N.colNames) {
                    N.colNames.push("流水号")
                }
            }
            if ($.inArray("createdBy", n)) {
                N.colModel.push({label: "创建者", name: "createdBy", width: 50, align: "center", hidden: true});
                if (N.colNames) {
                    N.colNames.push("创建者")
                }
            }
            if ($.inArray("createdDate", n)) {
                N.colModel.push({label: "创建时间", name: "createdDate", formatter: "timestamp", hidden: true});
                if (N.colNames) {
                    N.colNames.push("创建时间")
                }
            }
            if ($.inArray("lastModifiedBy", n)) {
                N.colModel.push({label: "最后更新者", name: "lastModifiedBy", width: 50, align: "center", hidden: true});
                if (N.colNames) {
                    N.colNames.push("最后更新者")
                }
            }
            if ($.inArray("lastModifiedDate", n)) {
                N.colModel.push({label: "最后更新时间", name: "lastModifiedDate", formatter: "timestamp", hidden: true});
                if (N.colNames) {
                    N.colNames.push("最后更新时间")
                }
            }
            N.colModel.push({label: "标题", name: "display", hidden: true, search: false, hidedlg: true});
            if (N.colNames) {
                N.colNames.push("标题")
            }
            $.each(N.colModel, function (ay, ax) {
                if (ax.frozen) {
                    T = true
                }
                ax = $.extend(true, {}, {
                    editoptions: {rows: 1},
                    searchoptions: {
                        clearSearch: false, searchhidden: true, defaultValue: "", buildSelect: function (aG) {
                            if (aG == null) {
                                aG = data
                            }
                            var aF = "<select>";
                            aF += "<option value=''></option>";
                            for (var aE in aG) {
                                aE = aE + "";
                                aF += ("<option value='" + aE + "'>" + aG[aE] + "</option>")
                            }
                            aF += "</select>";
                            return aF
                        }
                    }
                }, ax);
                if (ax.name.toLowerCase() == "todo") {
                    ax.formatter = function (aG, aE, aH, aF) {
                        return "TODO"
                    }
                }
                if (ax.responsive) {
                    if (ax.hidden == undefined) {
                        var aw = $(window).width();
                        var az = ax.responsive;
                        if (az == "sm") {
                            if (aw < 768) {
                                ax.hidden = true
                            }
                        } else {
                            if (az == "md") {
                                if (aw < 992) {
                                    ax.hidden = true
                                }
                            } else {
                                if (az == "lg") {
                                    if (aw < 1200) {
                                        ax.hidden = true
                                    }
                                }
                            }
                        }
                    }
                }
                if (ax.formatter == "currency") {
                    ax = $.extend({}, {width: 80, align: "right"}, ax);
                    ax.formatoptions = $.extend({}, ax.formatoptions, {
                        decimalSeparator: ".",
                        thousandsSeparator: ",",
                        decimalPlaces: 2,
                        prefix: "",
                        defaultValue: ""
                    });
                    ax.searchoptions = $.extend({}, ax.searchoptions, {sopt: ["eq", "ne", "ge", "le", "gt", "lt"]})
                }
                if (ax.formatter == "percentage") {
                    ax = $.extend(true, {width: 50, align: "right"}, ax);
                    ax.formatter = function (aG, aE, aH, aF) {
                        if (aG) {
                            return Math.round(aG * 10000) / 100 + "%"
                        } else {
                            return aG
                        }
                    }
                }
                if (ax.stype == "date" || ax.sorttype == "date" || ax.formatter == "date" || ax.formatter == "timestamp" || ax.formatter == "shortTimestamp") {
                    if (ax.formatter == "timestamp") {
                        ax = $.extend(true, {
                            width: 150,
                            fixed: true,
                            align: "center",
                            formatoptions: {srcformat: "Y-m-d H:i:s", newformat: "Y-m-d H:i:s"}
                        }, ax);
                        ax.formatter = "date"
                    } else {
                        if (ax.formatter == "shortTimestamp") {
                            ax = $.extend(true, {
                                width: 150,
                                fixed: true,
                                align: "center",
                                formatoptions: {srcformat: "Y-m-d H:i", newformat: "Y-m-d H:i"}
                            }, ax);
                            ax.formatter = "date"
                        } else {
                            ax = $.extend(true, {
                                width: 120,
                                fixed: true,
                                align: "center",
                                formatoptions: {newformat: "Y-m-d"}
                            }, ax)
                        }
                    }
                    ax.searchoptions = $.extend({}, ax.searchoptions, {
                        sopt: ["bt", "eq", "ne", "ge", "le", "gt", "lt"],
                        dataInit: function (aF) {
                            var aE = $(aF);
                            $(aF).daterangepicker($.extend(true, $.fn.daterangepicker.defaults, ax.searchoptions.daterangepicker), function (aH, aG) {
                                $(aF).focus();
                                ak.trigger("triggerToolbar")
                            });
                            $(aF).off("focus")
                        }
                    });
                    ax.editoptions = $.extend(ax.editoptions, {
                        dataInit: function (aE) {
                            if (ax.editoptions.time) {
                                $(aE).datetimepicker({
                                    language: "zh-CN",
                                    autoclose: true,
                                    todayBtn: true,
                                    minuteStep: 10,
                                    format: "yyyy-mm-dd hh:ii"
                                })
                            } else {
                                $(aE).datepicker({
                                    language: "zh-CN",
                                    autoclose: true,
                                    todayBtn: true,
                                    format: "yyyy-mm-dd"
                                })
                            }
                        }
                    })
                }
                if (ax.formatter == "showlink") {
                    ax = $.extend(true, {formatoptions: {idValue: "id", target: "modal-ajaxify"}}, ax)
                }
                if (ax.formatter == "integer") {
                    ax = $.extend(true, {
                        width: 60,
                        align: "center",
                        formatoptions: {defaultValue: "", thousandsSeparator: ""},
                        searchoptions: {sopt: ["eq", "ne", "ge", "le", "gt", "lt"]}
                    }, ax)
                }
                if (ax.formatter == "number") {
                    ax.sorttype = ax.formatter;
                    ax.edittype = ax.formatter;
                    ax = $.extend(true, {
                        width: 60,
                        align: "right",
                        formatoptions: {defaultValue: ""},
                        searchoptions: {sopt: ["eq", "ne", "ge", "le", "gt", "lt"]}
                    }, ax)
                }
                if (ax.name == "id") {
                    ax = $.extend(true, {
                        width: 80, align: "center", title: false, formatter: function (aH, aF, aJ, aG) {
                            if (aH && aH.length > 10) {
                                var aE = aH.length;
                                var aI = aH.substring(aE - 5, aE);
                                return "<span data='" + aH + "' onclick='$(this).html($(this).attr(\"data\"))'>..." + aI + "</span>"
                            } else {
                                return aH
                            }
                        }, frozen: true
                    }, ax);
                    ax.searchoptions = $.extend(true, ax.searchoptions, {sopt: ["eq", "ne", "ge", "le", "gt", "lt"]})
                }
                if (ax.formatter == "checkbox") {
                    ax.edittype = ax.formatter;
                    ax = $.extend(true, {width: 60, align: "center", formatter: "checkbox", stype: "select"}, ax);
                    ax.searchoptions.value = {"": "", "true": "是", "false": "否"};
                    ax.searchoptions.sopt = ["eq", "ne"];
                    ax.editoptions.value = "true:false"
                }
                if (ax.edittype == undefined || ax.edittype == "text" || ax.edittype == "select" || ax.edittype == "textarea") {
                    var c = ax.editoptions.dataInit;
                    ax.editoptions = $.extend(ax.editoptions, {
                        dataInit: function (aF) {
                            var aE = $(aF);
                            aE.removeClass("editable").addClass("form-control").attr("autocomplete", "off").css({width: "100%"});
                            if (c) {
                                c.call(this, aF)
                            }
                            if (ax.editoptions.updatable == false) {
                                var aG = ak.jqGrid("getSelectedRowdata");
                                if (aG && aG.id) {
                                    aE.attr("disabled", true)
                                } else {
                                    if (!aE.attr("placeholder")) {
                                        aE.attr("placeholder", "创建后不可修改");
                                        aE.attr("title", "创建后不可修改")
                                    }
                                }
                            }
                            if (aE.is("input[type='text']")) {
                                aE.blur(function () {
                                    aE.val($.trim(aE.val()))
                                })
                            }
                            if (aE.is("select")) {
                                aE.select2({
                                    openOnEnter: false, placeholder: "请选择...", matcher: function (aI, aL) {
                                        var aH = Pinyin.getCamelChars(aL) + "";
                                        var aK = aH.toUpperCase().indexOf(aI.toUpperCase()) == 0;
                                        var aJ = aL.toUpperCase().indexOf(aI.toUpperCase()) == 0;
                                        return (aK || aJ)
                                    }
                                })
                            }
                            if (ax.editoptions.spellto) {
                                aE.change(function () {
                                    var aH = {};
                                    aH[ax.editoptions.spellto] = Pinyin.getCamelChars($.trim(aE.val()));
                                    ak.jqGrid("setEditingRowdata", aH)
                                })
                            }
                        }
                    })
                }
                if (ax.formatter == "select") {
                    if (ax.searchoptions.sopt == undefined) {
                        ax.searchoptions.sopt = ["eq", "ne"]
                    }
                    if (ax.edittype == undefined) {
                        ax.edittype = "select"
                    }
                    if (ax.stype == undefined) {
                        ax.stype = "select"
                    }
                    if (typeof ax.searchoptions.value === "function") {
                        ax.searchoptions.value = ax.searchoptions.value.call(ak)
                    }
                    if (ax.searchoptions.valueJsonString) {
                        ax.searchoptions.value = {"": ""};
                        var aD = $.parseJSON(ax.searchoptions.valueJsonString);
                        for (var aC in aD) {
                            ax.searchoptions.value[aC] = aD[aC]
                        }
                    }
                    ax.editoptions = $.extend(true, {value: ax.searchoptions.value}, ax.editoptions)
                }
                if (!ax.hidden) {
                    if (ax.width) {
                        U += ax.width
                    } else {
                        U += 300
                    }
                }
                if (ax.hasOwnProperty("searchoptions")) {
                    var aA = ax.searchoptions;
                    if (aA.hasOwnProperty("defaultValue") && aA.defaultValue != "") {
                        var aB = ax.index;
                        if (aB == undefined) {
                            aB = ax.name
                        }
                        V[V.length++] = {field: aB, op: ax.searchoptions.sopt[0], data: aA.defaultValue}
                    }
                }
                if (ax.searchoptions.sopt == undefined) {
                    ax.searchoptions.sopt = ["cn", "bw", "bn", "eq", "ne", "nc", "ew", "en"]
                }
                if (ax.index == undefined) {
                    if (ax.formatter == "date" || ax.formatter == "timestamp") {
                        ax.index = ax.name + "@Date"
                    } else {
                        if (ax.formatter == "checkbox") {
                            ax.index = ax.name + "@Boolean"
                        } else {
                            if (ax.formatter == "integer" || ax.formatter == "currency" || ax.formatter == "number" || ax.formatter == "percentage") {
                                ax.index = ax.name + "@Number"
                            }
                        }
                    }
                }
                N.colModel[ay] = ax
            });
            if (d == "items") {
                N.colModel.push({name: "extraAttributes.dirtyRow", hidden: true, hidedlg: true});
                if (N.colNames) {
                    N.colNames.push("extraAttributes.dirtyRow")
                }
                N.colModel.push({name: "_arrayIndex", hidedlg: true, hidden: true});
                if (N.colNames) {
                    N.colNames.push("_arrayIndex")
                }
                N.colModel.push({name: "extraAttributes.operation", hidedlg: true, hidden: true});
                if (N.colNames) {
                    N.colNames.push("extraAttributes.operation")
                }
            }
            var r = $(".theme-panel .grid-shrink-option").val();
            if (r == "true") {
                N.shrinkToFit = true
            } else {
                if (Number(U) > Number(ak.parent().width())) {
                    $.each(N.colModel, function (aw, c) {
                        if (!c.hidden) {
                            if (c.width == undefined) {
                                c.width = 300
                            }
                        }
                    });
                    N.shrinkToFit = false
                }
            }
            var aj = false;
            if (ak.closest(".ui-subgrid").size() == 0 && d != "items") {
                if (N.height == undefined || N.height == "stretch") {
                    aj = true;
                    N.height = 0
                }
            }
            if (N.filterToolbar) {
                if (N.postData == undefined) {
                    N.postData = {}
                }
                var E = N.postData;
                var C = {};
                if (E.hasOwnProperty("filters")) {
                    C = JSON.parse(E.filters)
                }
                var o = [];
                if (C.hasOwnProperty("rules")) {
                    o = C.rules
                }
                $.each(V, function (aw, c) {
                    var ax = false;
                    $.each(o, function (ay, az) {
                        if (c.field == az.field) {
                            ax = true;

                        }
                    });
                    if (ax == false) {
                        o.push(c)
                    }
                });
                if (o.length > 0) {
                    C.groupOp = "AND";
                    C.rules = o;
                    E._search = true;
                    E.filters = JSON.stringify(C)
                }
            }
            if (N.jqPivot) {
                var S = N.jqPivot;
                delete N.jqPivot;
                var m = N.url;
                N = {multiselect: false, pager: N.pager, shrinkToFit: false};
                ak.jqGrid("jqPivot", m, S, N, {reader: "content"});
                return
            } else {
                ak.jqGrid(N)
            }
            if (N.filterToolbar) {
                ak.jqGrid("filterToolbar", N.filterToolbar);
                var F = $("#jqgh_" + ak.attr("id") + "_rn");
                var R = '<a href="javascript:;" title="显示快速查询"><span class="ui-icon ui-icon-carat-1-s"></span></a>';
                var Z = '<a href="javascript:;" title="隐藏快速查询"><span class="ui-icon ui-icon-carat-1-n"></span></a>';
                if (ak.is(".ui-jqgrid-subgrid") || N.filterToolbar == "hidden") {
                    F.html(R);
                    ak[0].toggleToolbar()
                } else {
                    F.html(Z)
                }
                F.on("click", ".ui-icon-carat-1-s", function () {
                    F.html(Z);
                    ak[0].toggleToolbar()
                });
                F.on("click", ".ui-icon-carat-1-n", function () {
                    F.html(R);
                    ak[0].toggleToolbar()
                })
            }
            if (N.setGroupHeaders) {
                ak.jqGrid("setGroupHeaders", $.extend(true, {useColSpanStyle: true}, N.setGroupHeaders))
            }
            ak.bindKeys({
                upKey: false, downKey: false, onEnter: function (aw) {
                    if (aw == undefined) {
                        return
                    }
                    ak.find("tr.jqgrow").attr("tabindex", -1);
                    var c = ak.find("tr.jqgrow[id='" + aw + "']");
                    c.attr("tabindex", 0);
                    if (N.editurl) {
                        if (c.attr("editable") == "1") {
                            O.find(".ui-pg-div span.ui-icon-disk").click()
                        } else {
                            O.find(".ui-pg-div span.ui-icon-pencil").click()
                        }
                        return false
                    }
                }
            });
            if (N.pager || N.toppager) {
                var O = $(N.pager);
                var au = Util.notSmallViewport();
                if (au) {
                    au = (d == "items" ? false : true)
                }
                var am = Util.notSmallViewport();
                if (am) {
                    am = (d == "items" ? false : true)
                }
                ak.jqGrid("navGrid", N.pager, {
                    edit: false,
                    add: false,
                    del: false,
                    refresh: au,
                    search: au,
                    position: "right",
                    cloneToTop: true
                });
                if (N.columnChooser) {
                    var e = {
                        caption: "",
                        buttonicon: "ui-icon-battery-2",
                        position: "first",
                        title: "设定显示列和顺序",
                        onClickButton: function () {
                            var c = ak.jqGrid("getGridParam", "width");
                            ak.jqGrid("columnChooser", {
                                width: 470, done: function (aw) {
                                    if (aw) {
                                        this.jqGrid("remapColumns", aw, true);
                                        ak.jqGrid("setGridWidth", c, false)
                                    } else {
                                    }
                                }
                            })
                        }
                    };
                    if (N.pager) {
                        ak.jqGrid("navButtonAdd", N.pager, e)
                    }
                    if (N.toppager) {
                        ak.jqGrid("navButtonAdd", N.toppager, e)
                    }
                }
                var at = Util.notSmallViewport();
                if (at) {
                    at = (d == "items" ? false : true)
                }
                if (N.exportExcelLocal && at) {
                    var z = {
                        caption: "",
                        buttonicon: "ui-icon-arrowthickstop-1-s",
                        position: "first",
                        title: "导出当前显示数据",
                        onClickButton: function () {
                            ak.jqGrid("exportExcelLocal", N.exportExcelLocal)
                        }
                    };
                    if (N.pager) {
                        ak.jqGrid("navButtonAdd", N.pager, z)
                    }
                    if (N.toppager) {
                        ak.jqGrid("navButtonAdd", N.toppager, z)
                    }
                }
                var P = {
                    caption: "",
                    buttonicon: "ui-icon-arrowstop-1-w",
                    position: "first",
                    title: "收缩显示模式",
                    onClickButton: function () {
                        var c = ak.jqGrid("getGridParam", "width");
                        ak.jqGrid("destroyFrozenColumns");
                        ak.jqGrid("setGridWidth", c, true)
                    }
                };
                if (N.pager) {
                    ak.jqGrid("navButtonAdd", N.pager, P)
                }
                if (N.toppager) {
                    ak.jqGrid("navButtonAdd", N.toppager, P)
                }
                if (N.gridDnD) {
                    var an = $.extend(true, {
                        dropbyname: true,
                        beforedrop: function (c, ax, aw) {
                            aw.id = $(ax.draggable).attr("id");
                            return aw
                        },
                        autoid: function (c) {
                            return c.id
                        },
                        drop_opts: {activeClass: "ui-state-active", hoverClass: "ui-state-hover", greedy: true},
                        ondrop: function (c, az, aC) {
                            var aD = $("#" + this.id);
                            var aE = aD.closest(".ui-subgrid");
                            var ay = "";
                            if (aE.size() > 0) {
                                ay = aE.prev(".jqgrow").attr("id")
                            }
                            var aw = $(az.draggable).attr("id");
                            var aA = {};
                            var aB = aD.jqGrid("getGridParam", "parent");
                            var ax = aD.jqGrid("getGridParam", "editurl");
                            if (ax) {
                                aA[aB] = ay;
                                aA.id = aw;
                                aD.ajaxPostURL({
                                    url: ax, success: function () {
                                        return true
                                    }, confirmMsg: false, data: aA
                                })
                            }
                        }
                    }, N.gridDnD);
                    var v = {
                        caption: "",
                        buttonicon: "ui-icon-arrow-4",
                        position: "first",
                        title: "开启拖放移动模式",
                        onClickButton: function () {
                            var c = null;
                            if (ak.closest(".ui-subgrid").size() > 0) {
                                $topGrid = ak.parent().closest(".ui-jqgrid-btable:not(.ui-jqgrid-subgrid)");
                                c = $topGrid.parent().find(".ui-jqgrid-btable")
                            } else {
                                c = ak.parent().find(".ui-jqgrid-btable")
                            }
                            var ax = [];
                            c.each(function (ay, az) {
                                ax.push("#" + $(this).attr("id"))
                            });
                            var aw = ax.reverse();
                            $.each(aw, function (az, aC) {
                                var aB = $.map(ax, function (aD) {
                                    return aD != aC ? aD : null
                                });
                                var ay = $(aC);
                                if (aB.length > 0) {
                                    var aA = $.extend({connectWith: aB.join(",")}, an);
                                    ay.jqGrid("gridDnD", aA)
                                }
                                if (!ay.hasClass("ui-jqgrid-dndtable")) {
                                    ay.addClass("ui-jqgrid-dndtable")
                                }
                            })
                        }
                    };
                    if (N.pager) {
                        ak.jqGrid("navButtonAdd", N.pager, v)
                    }
                    if (N.toppager) {
                        ak.jqGrid("navButtonAdd", N.toppager, v)
                    }
                }
                if (N.pager && (N.inlineNav.add || N.inlineNav.edit || N.inlineNav.del) && N.inlineNav != false) {
                    ak.jqGrid("inlineNav", N.pager, N.inlineNav)
                }
                O.find(".navtable").css("float", "right");
                var x = O.find(" .navtable > tbody > tr");
                ak.jqGrid("navSeparatorAdd", N.pager, {position: "first"});
                var h = $("<td></td>").prependTo(x);
                var ag = $('<div class="btn-group dropup btn-group-contexts"><button data-close-others="true" data-delay="1000" data-toggle="dropdown" class="btn btn-xs yellow dropdown-toggle" type="button"><i class="fa fa-cog"></i>  <i class="fa fa-angle-down"></i></button></div>');
                h.append(ag);
                ag.wrap('<div class="clearfix jqgrid-options"></div>');
                Y = $('<ul role="menu" class="dropdown-menu"></ul>');
                Y.appendTo(ag);
                var Q = [];
                var I = [];
                var B = [];
                if (N.viewurl) {
                    var t = $('<li><a href="javascript:;"><i class="fa fa-credit-card"></i> 查看详情</a></li>');
                    t.children("a").bind("click", function (ax) {
                        ax.preventDefault();
                        var az = ak.getOnlyOneSelectedItem();
                        if (az) {
                            var c = "TBD";
                            var ay = ak.jqGrid("getRowData", az);
                            if (N.editcol) {
                                c = ay[N.editcol];
                                if (c.indexOf("<") > -1 && c.indexOf(">") > -1) {
                                    c = $(c).text()
                                }
                            } else {
                                c = ay.id;
                                if (c.indexOf("<span") > -1) {
                                    c = $(c).text()
                                }
                            }
                            var aw = Util.AddOrReplaceUrlParameter(N.viewurl, "id", az);
                            t.popupDialog({title: "查看: " + c, url: aw})
                        }
                    });
                    I.push(t)
                }
                if (N.fullediturl) {
                    if (N.addable == undefined || N.addable != false) {
                        var ae = $('<li><a href="javascript:;"><i class="fa fa-plus-square"></i> 新增数据</a></li>').appendTo(Y);
                        ae.children("a").bind("click", function (c) {
                            c.preventDefault();
                            av.popupDialog({title: "新增", url: N.fullediturl})
                        });
                        Q.push(ae);
                        var av = $('<li><a href="javascript:;"><i class="fa fa-copy"></i> 克隆复制</a></li>');
                        av.children("a").bind("click", function (ax) {
                            ax.preventDefault();
                            var ay = ak.getOnlyOneSelectedItem();
                            if (ay) {
                                var c = N.cloneurl ? N.cloneurl : N.fullediturl;
                                var aw = Util.AddOrReplaceUrlParameter(c, "id", ay);
                                aw = aw + ("&clone=true");
                                av.popupDialog({title: "克隆复制", url: aw})
                            }
                        });
                        I.push(av)
                    }
                    var s = $('<li><a href="javascript:;"><i class="fa fa-edit"></i> 编辑数据 <span class="badge badge-info">双击</span></a></li>');
                    s.children("a").bind("click", function (ax) {
                        ax.preventDefault();
                        var az = ak.getOnlyOneSelectedItem();
                        if (az) {
                            var c;
                            var ay = ak.jqGrid("getRowData", az);
                            if (N.editcol) {
                                c = ay[N.editcol];
                                if (c && c.indexOf("<") > -1 && c.indexOf(">") > -1) {
                                    c = $(c).text()
                                }
                            } else {
                                c = ay.id;
                                if (c.indexOf("<span") > -1) {
                                    c = $(c).text()
                                }
                            }
                            if (c == undefined) {
                                c = "TBD"
                            }
                            var aw = Util.AddOrReplaceUrlParameter(N.fullediturl, "id", az);
                            s.popupDialog({url: aw, title: "编辑：" + c})
                        }
                    });
                    I.push(s)
                }
                if (N.operations) {
                    var q = [];
                    N.operations.call(ak, q);
                    $.each(q, function () {
                        var c = $(this);
                        var aw = c.attr("data-position");
                        if (aw == "multi") {
                            B.push(c)
                        } else {
                            if (aw == "single") {
                                I.push(c)
                            } else {
                                Q.push(c)
                            }
                        }
                    })
                }
                if (Q.length > 0) {
                    $.each(Q, function () {
                        var az = $(this);
                        var aw = az.children("a");
                        az.appendTo(Y);
                        if (Util.notSmallViewport()) {
                            var ay = aw.children("i").attr("class");
                            var c = "";
                            if (az.attr("data-text") == "show") {
                                c = aw.text()
                            }
                            var ax = $('<button type="button" class="btn btn-xs blue" style="margin-left:5px"><i class="' + ay + '"></i> ' + c + "</button>").appendTo(ag.parent());
                            ax.attr("title", az.text());
                            ax.click(function () {
                                aw.click()
                            })
                        }
                    })
                }
                if (I.length > 0) {
                    if (Y.find("li").size() > 0) {
                        Y.append('<li class="divider"></li>')
                    }
                    $.each(I, function () {
                        var ax = $(this);
                        var c = ax.children("a");
                        ax.appendTo(Y);
                        if (Util.notSmallViewport() && ax.attr("data-toolbar") == "show") {
                            var c = ax.children("a");
                            var aw = c.clone();
                            aw.addClass("btn btn-xs blue");
                            aw.css({"margin-left": "5px"});
                            aw.appendTo(ag.parent());
                            aw.click(function (ay) {
                                c.click();
                                ay.preventDefault();
                                return false
                            })
                        }
                    })
                }
                if (B.length > 0) {
                    if (Y.find("li").size() > 0) {
                        Y.append('<li class="divider"></li>')
                    }
                    $.each(B, function () {
                        var ax = $(this);
                        ax.appendTo(Y);
                        if (Util.notSmallViewport() && ax.attr("data-toolbar") == "show") {
                            var c = ax.children("a");
                            var aw = c.clone();
                            aw.addClass("btn btn-xs blue");
                            aw.css({"margin-left": "5px"});
                            aw.appendTo(ag.parent());
                            aw.click(function (ay) {
                                c.click();
                                ay.preventDefault();
                                return false
                            })
                        }
                    })
                }
                if (Y.find("li").length == 0) {
                    ag.hide()
                } else {
                    Y.find("li > a").each(function (c) {
                        $(this).attr("role-idx", c)
                    })
                }
                if (!Util.notSmallViewport()) {
                    var p = O.find(" > .ui-pager-control > .ui-pg-table > tbody");
                    var al = p.find(" > tr > td").eq(0);
                    al.attr("align", "left");
                    $("<tr/>").appendTo(p).append(al);
                    var al = p.find(" > tr > td").eq(0);
                    al.attr("align", "left");
                    $("<tr/>").appendTo(p).append(al);
                    var al = p.find(" > tr > td").eq(0);
                    al.find("> .ui-pg-table").css("float", "left");
                    O.height("75px")
                } else {
                    O.find("#" + O.attr("id") + "_left").css({width: "150px"})
                }
                if (N.pager && N.toppager) {
                    var u = ak.attr("id") + "_toppager";
                    var M = $("#" + u);
                    ak.jqGrid("navSeparatorAdd", "#" + u, {position: "first"});
                    var b = $("div#" + u + " .ui-pg-table > tbody > tr");
                    var H = b.find("#" + u + "_right");
                    var ar = O.find(".jqgrid-options").parent("td").clone(true);
                    ar.prependTo(H.find("> .ui-pg-table > tbody > tr"));
                    ar.find(".btn-group").removeClass("dropup");
                    H.prependTo(H.parent());
                    var g = b.find("#" + u + "_left");
                    g.css({width: "150px"});
                    g.appendTo(g.parent());
                    var W = g.find(".ui-paging-info");
                    W.css("float", "right");
                    M.width(O.width());
                    if (!Util.notSmallViewport()) {
                        M.hide()
                    }
                    if (ak.closest(".ui-subgrid").size() > 0) {
                        $(N.pager).hide()
                    }
                }
                var ab = $(".theme-panel .context-menu-option").val();
                if (ab == "enable" && N.contextMenu && Y.find("li").length > 0) {
                    var ad = $('<div id="' + ac + '" class="context-menu"></div>');
                    Y.clone().appendTo(ad);
                    $("body").append(ad);
                    ak.unbind("contextmenu")
                }
            }
            var L = X.jqGrid("getGridParam", "colModel");
            for (var ah = 0; ah < L.length; ah++) {
                var aq = L[ah];
                if (aq.tooltips) {
                    var ai = $('<span class="glyphicon glyphicon-exclamation-sign tooltipster"  title="' + aq.tooltips + '"></span>');
                    var w = aq.index ? aq.index : aq.name;
                    var af = $(".ui-jqgrid-sortable[id*='" + w + "']", A);
                    if (af.size() > 0) {
                        af.prepend(ai);
                        ai.tooltipster({contentAsHTML: true, offsetY: 5, theme: "tooltipster-punk"})
                    }
                }
            }
            var ap = N.editrulesurl;
            if (ap) {
                var A = $("#gbox_" + X.attr("id") + "  .ui-jqgrid-labels");
                A.ajaxJsonUrl(ap, function (az) {
                    var aw = X.jqGrid("getGridParam", "colModel");
                    for (var ay in az) {
                        for (var ax = 0; ax < aw.length; ax++) {
                            var aC = aw[ax];
                            if ((aC.index && aC.index == ay) || (aC.name && aC.name == ay)) {
                                aw[ax].editrules = $.extend(az[ay] || {}, aw[ax].editrules || {});
                                if (aw[ax].editrules.required == undefined) {
                                    aw[ax].editrules.required = false
                                }
                                delete aC.editrules.timestamp;
                                delete aC.editrules.shortTimestamp;
                                if (aC.editrules.tooltips && aC.tooltips == undefined) {
                                    var aB = $('<span class="glyphicon glyphicon-exclamation-sign tooltipster"  title="' + aC.editrules.tooltips + '"></span>');
                                    var aA = $(".ui-jqgrid-sortable[id*='" + ay + "']", A);
                                    if (aA.size() > 0) {
                                        aA.prepend(aB);
                                        aB.tooltipster({contentAsHTML: true, offsetY: 5, theme: "tooltipster-punk"})
                                    }
                                    delete aC.editrules.tooltips
                                }
                                break
                            }
                        }
                    }
                })
            }
            if (aj) {
                var D = $("#gbox_" + X.attr("id"));
                var ao = 0;
                var aa = "div.ui-jqgrid-titlebar,div.ui-jqgrid-hdiv,div.ui-jqgrid-pager,div.ui-jqgrid-toppager,div.ui-jqgrid-sdiv";
                D.find(aa).filter(":visible").each(function () {
                    ao += $(this).outerHeight()
                });
                ao = ao + 4;
                var l = $(window).height() - ak.closest(".ui-jqgrid").offset().top - ao;
                if (l < 300) {
                    l = 300
                }
                ak.setGridHeight(l, true)
            }
            Grid.refreshWidth();
            if (T) {
                ak.jqGrid("setFrozenColumns")
            }
            ak.jqGrid("gridResize", {minWidth: 500, minHeight: 100});
            ak.closest(".ui-jqgrid").find(".ui-resizable-s").dblclick(function () {
                var c = ak.jqGrid("getGridParam", "height");
                ak.jqGrid("setGridHeight", ak.height() + 17)
            }).attr("title", "鼠标双击可自动扩展显示区域")
        }, refreshWidth: function () {
            $("table.ui-jqgrid-btable:visible").each(function () {
                var c = $(this);
                var d = c.jqGrid("getGridParam", "width");
                var b = c.closest("div.ui-jqgrid").parent("div").width();
                if (d != b) {
                    c.jqGrid("setGridWidth", b);
                    var e = $(this).jqGrid("getGridParam", "groupHeader");
                    if (e) {
                        c.jqGrid("destroyGroupHeader");
                        c.jqGrid("setGroupHeaders", e)
                    }
                }
            })
        }, initRecursiveSubGrid: function (f, c, e, h) {
            var b = $("<table data-grid='table' class='ui-jqgrid-subgrid'/>").appendTo($("#" + f));
            var d = b.closest("table.ui-jqgrid-btable").data("gridOptions");
            d.url = Util.AddOrReplaceUrlParameter(d.url, "search['EQ_" + e + "']", c);
            if (d.fullediturl) {
                d.fullediturl = Util.AddOrReplaceUrlParameter(d.fullediturl, e, c)
            }
            d.inlineNav = $.extend(true, {addParams: {addRowParams: {extraparam: {}}}}, d.inlineNav);
            d.inlineNav.addParams.addRowParams.extraparam[e] = c;
            d.parent = e;
            if (h) {
                d.postData = {}
            }
            b.data("gridOptions", d);
            Grid.initGrid(b);
            var g = $("#" + f).parent().closest(".ui-jqgrid-btable:not(.ui-jqgrid-subgrid)");
            if (d.gridDnD && g.hasClass("ui-jqgrid-dndtable")) {
                $("#" + f).find(".ui-icon-arrow-4:first").click()
            }
        }, initSubGrid: function (e, d, c) {
            var b = $("<table data-grid='table' class='ui-jqgrid-subgrid'/>").appendTo($("#" + e));
            b.data("gridOptions", c);
            Grid.initGrid(b)
        }
    }
}();