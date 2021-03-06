var Page = function () {
    return {
        initDynamicTable: function ($container) {
            $("table[data-dynamic-table]", $container).each(function () {
                var $dynamicTable = $(this);
                var options = $dynamicTable.data("dynamicTableOptions");
                $(this).dynamictable(options)
            })
        }, initDropdownSelect: function ($container) {
            $('input[data-toggle="dropdown-select"]', $container).each(function () {
                var $el = $(this);
                if ($el.attr("dropdown-select-done")) {
                    return this
                }
                $el.attr("dropdown-select-done", true);
                var $toggle = $('<i class="fa fa-angle-double-down btn-toggle"></i>').insertBefore($el);
                var $clear = $('<i class="fa fa-times"></i>').insertBefore($el);
                var $elems = $el.parent().children();
                $elems.wrapAll('<div class="input-icon right"></div>');
                $clear.click(function () {
                    $elems.val("")
                });
                $el.attr("autocomplete", "off");
                var $parent = $el.closest(".panel-content");
                var $dropdown = $('<div class="container-dropdownselect" style="display : none;background-color: white;border: 1px solid #CCCCCC;"></div>');
                if ($parent.size() > 0) {
                    $dropdown.appendTo($parent)
                } else {
                    $dropdown.appendTo($("body"))
                }
                if ($el.attr("data-min-width")) {
                    $dropdown.css("min-width", $el.attr("data-min-width"))
                }
                $el.add($el.parent().find("i.btn-toggle")).click(function () {
                    var parentOffsetLeft = 0;
                    var parentOffsetTop = 0;
                    if ($parent.size() > 0) {
                        parentOffsetLeft = $parent.offset().left;
                        parentOffsetTop = $parent.offset().top
                    }
                    var offset = $el.offset();
                    $dropdown.css({
                        width: $el.outerWidth(),
                        position: "absolute",
                        left: (offset.left - parentOffsetLeft) + "px",
                        top: (offset.top - parentOffsetTop) + $el.outerHeight() + "px"
                    });
                    $dropdown.on("");
                    $dropdown.data("callback", $el.data("data-dropdownselect-callback"));
                    $dropdown.slideToggle("fast");
                    var $btnToggle = $el.parent().find("i.btn-toggle");
                    if ($btnToggle.hasClass("fa-angle-double-down")) {
                        $btnToggle.removeClass("fa-angle-double-down");
                        $btnToggle.addClass("fa-angle-double-up")
                    } else {
                        $btnToggle.addClass("fa-angle-double-down");
                        $btnToggle.removeClass("fa-angle-double-up")
                    }
                    if ($dropdown.is(":empty")) {
                        var url = $el.attr("data-url");
                        if ($el.attr("data-selected")) {
                            if (url.indexOf("?") > -1) {
                                url = url + "&selected=" + $el.attr("data-selected")
                            } else {
                                url = url + "?selected=" + $el.attr("data-selected")
                            }
                        }
                        $dropdown.ajaxGetUrl(url)
                    }
                });
                $(document).on("mousedown", function (e) {
                    var $p = $el.parent("div");
                    if (!($p.is(e.target) || $p.find(e.target).length || $dropdown.is(e.target) || $dropdown.find(e.target).length)) {
                        $dropdown.hide()
                    }
                })
            })
        }, initMultiSelectDouble: function ($container) {
            if ($.fn.multiSelect) {
                $('select[data-toggle="double-multi-select"]', $container).each(function () {
                    var $select = $(this);
                    if ($select.attr("double-multi-select-done")) {
                        return this
                    }
                    $select.attr("double-multi-select-done", true);
                    $select.multiSelect({
                        selectableHeader: "<input type='text' class='form-control search-input' autocomplete='off' placeholder='快速过滤...'>",
                        selectionHeader: "<input type='text' class='form-control search-input' autocomplete='off' placeholder='快速过滤...'>",
                        afterInit: function (ms) {
                            var that = this;
                            var $selectableSearch = that.$selectableUl.prev();
                            var $selectionSearch = that.$selectionUl.prev();
                            var selectableSearchString = "#" + that.$container.attr("id") + " .ms-elem-selectable:not(.ms-selected)";
                            var selectionSearchString = "#" + that.$container.attr("id") + " .ms-elem-selection.ms-selected";
                            that.qs1 = $selectableSearch.quicksearch(selectableSearchString).on("keydown", function (e) {
                                if (e.which === 40) {
                                    that.$selectableUl.focus();
                                    return false
                                }
                            });
                            that.qs2 = $selectionSearch.quicksearch(selectionSearchString).on("keydown", function (e) {
                                if (e.which == 40) {
                                    that.$selectionUl.focus();
                                    return false
                                }
                            });
                            var height = $select.attr("data-height");
                            if (height) {
                                that.$container.find(".ms-list").css({height: height})
                            }
                        },
                        afterSelect: function () {
                            this.qs1.cache();
                            this.qs2.cache()
                        },
                        afterDeselect: function () {
                            this.qs1.cache();
                            this.qs2.cache()
                        }
                    })
                })
            }
        }, initSelect2: function ($container) {
            if (jQuery().select2) {
                $('select:not([data-toggle="double-multi-select"])', $container).each(function () {
                    var $select2 = $(this);
                    if ($select2.attr("select2-done")) {
                        return this
                    }
                    $select2.attr("select2-done", true);
                    if ($select2.closest(".ui-search-input,.ui-pager-control").size() > 0) {
                        return
                    }
                    if ($select2.find(' > option[value=""]').size() == 0) {
                        var $empty = $('<option value=""></option>');
                        $select2.prepend($empty)
                    }
                    if ($select2.find(" > option[selected]").size() == 0) {
                        $select2.find("> option[value='']").attr("selected", true)
                    }
                    var allowClear = true;
                    if ($select2.attr("data-allow-clear")) {
                        if ($select2.attr("data-allow-clear") == "false") {
                            allowClear = false
                        }
                    }
                    var placeholder = $select2.attr("placeholder");
                    if (placeholder == undefined) {
                        placeholder = "请选择..."
                    }
                    var options = {
                        placeholder: placeholder, allowClear: allowClear, matcher: function (term, text) {
                            var mod = Pinyin.getCamelChars(text) + "";
                            var tf1 = mod.toUpperCase().indexOf(term.toUpperCase()) == 0;
                            var tf2 = text.toUpperCase().indexOf(term.toUpperCase()) == 0;
                            return (tf1 || tf2)
                        }
                    };
                    if ($select2.attr("data-select2-type") == "combobox") {
                        var $input = $('<input type="text" name="' + $(this).attr("name") + '"/>').insertAfter($select2);
                        if ($select2.attr("class") != undefined) {
                            $input.attr("class", $select2.attr("class"))
                        }
                        var selected = $select2.find("option:selected").val();
                        options = $.extend({}, options, {
                            placeholder: "请选择或输入...",
                            allowClear: true,
                            query: function (query) {
                                var data = {results: []};
                                $select2.find("option").each(function () {
                                    data.results.push({id: $(this).val(), text: $(this).text()})
                                });
                                query.callback(data)
                            },
                            initSelection: function (element, callback) {
                                if (selected != undefined) {
                                    var data = {id: selected, text: selected};
                                    callback(data)
                                }
                            },
                            createSearchChoice: function (term, data) {
                                if ($(data).filter(function () {
                                        return this.text.localeCompare(term) === 0
                                    }).length === 0) {
                                    return {id: term, text: term}
                                }
                            }
                        });
                        $input.select2(options);
                        if (selected != undefined) {
                            $input.select2("val", [selected])
                        }
                        $select2.remove()
                    } else {
                        var dataCache = $select2.attr("data-cache");
                        if (dataCache) {
                            var dataCache = eval(dataCache);
                            for (var i in dataCache) {
                                $select2.append("<option value='" + i + "'>" + dataCache[i] + "</option>")
                            }
                        }
                        var dataUrl = $select2.attr("data-url");
                        if (dataUrl) {
                            var val = $select2.val();
                            var dataCache = Util.getCacheSelectOptionDatas(WEB_ROOT + dataUrl);
                            for (var i in dataCache) {
                                if (val && val == i) {
                                    continue
                                }
                                $select2.append("<option value='" + i + "'>" + dataCache[i] + "</option>")
                            }
                        }
                        $select2.select2(options)
                    }
                    var $container = $select2.parent(".controls").children(".select2-container");
                    if (!$container.hasClass("form-control")) {
                        $container.addClass("form-control")
                    }
                    if ($select2.attr("required") == "required" || $select2.attr("required") == "true" || $select2.attr("data-rule-required") == "true") {
                        $select2.on("select2-close", function (e) {
                            $select2.valid()
                        })
                    }
                })
            }
        }, initSelect2Remote: function ($container) {
            if (jQuery().select2) {
                $('input[data-select2-type="remote"]', $container).each(function () {
                    var $select2 = $(this);
                    if ($select2.attr("select2-done")) {
                        return this
                    }
                    $select2.attr("select2-done", true);
                    var options = {
                        placeholder: $select2.attr("data-display"),
                        minimumInputLength: 1,
                        ajax: {
                            url: $select2.attr("data-url"), dataType: "json", data: function (term, page) {
                                var data = {};
                                data[$select2.attr("data-query")] = term;
                                return data
                            }, results: function (data, page) {
                                return {results: data.content}
                            }
                        },
                        formatResult: function (item) {
                            return item.display
                        },
                        formatSelection: function (item) {
                            $select2.val(item.id);
                            return item.display
                        }
                    };
                    $select2.select2(options)
                })
            }
        }, initSelect2Tags: function ($container) {
            if (jQuery().select2) {
                function splitVal(string, separator) {
                    var val, i, l;
                    if (string === null || string.length < 1) {
                        return []
                    }
                    val = string.split(separator);
                    for (i = 0, l = val.length; i < l; i = i + 1) {
                        val[i] = $.trim(val[i])
                    }
                    return val
                }

                $('input[data-select2-type="tags"]', $container).each(function () {
                    var $select2 = $(this);
                    if ($select2.attr("select2-done")) {
                        return this
                    }
                    $select2.attr("select2-done", true);
                    var options = {
                        tags: true, tokenSeparators: [","], initSelection: function (element, callback) {
                            var data = [];
                            $(splitVal(element.val(), ",")).each(function () {
                                data.push({id: this, text: this})
                            });
                            callback(data)
                        }, formatSelectionTooBig: function (limit) {
                            return "最大允许个数：" + limit
                        }
                    };
                    var tags = $select2.attr("data-tags");
                    if (tags) {
                        var data = [];
                        $(splitVal(tags, ",")).each(function () {
                            data.push(this)
                        });
                        options.tags = data
                    } else {
                        var url = $select2.attr("data-url");
                        if (url) {
                            options.ajax = {
                                url: url, dataType: "json", data: function (term, page) {
                                    return {q: term}
                                }, results: function (data, page) {
                                    return {results: data}
                                }
                            }
                        }
                    }
                    var limit = $select2.attr("data-number-limit");
                    if (limit) {
                        options.maximumSelectionSize = limit
                    }
                    $select2.select2(options)
                })
            }
        }, initUploadSingleFile: function ($container) {
            $('input[data-upload="single-file"]', $container).each(function () {
                var $el = $(this);
                if ($el.attr("single-file-done")) {
                    return this
                }
                $el.attr("single-file-done", true);
                var $upload = $('<span class="input-group-btn"><button type="button" class="btn default"><i class="fa fa-arrow-circle-o-up"></i></button></span>').insertAfter($el);
                var $download = $('<span class="input-group-btn"><button type="button" class="btn default"><i class="fa fa-arrow-circle-o-down"></i></button></span>').insertAfter($el);
                var $elems = $el.parent().children();
                $elems.wrapAll('<div class="input-group"></div>');
                $upload.click(function () {
                    Global.triggerSingleFileUpload($el)
                });
                $download.click(function () {
                    var path = $el.val();
                    if (path != "") {
                        window.open(READ_FILE_URL_PREFIX + $el.val())
                    }
                })
            })
        }, initDatePicker: function ($container) {
            $('input[data-picker="date"]', $container).each(function () {
                var $datapicker = $(this);
                if ($datapicker.attr("data-picker-done")) {
                    return this
                }
                $datapicker.attr("data-picker-done", true);
                if ($datapicker.attr("readonly") || $datapicker.attr("disabled")) {
                    return
                }
                $datapicker.wrap('<div class="input-icon right"></div>');
                $datapicker.before('<i class="fa fa-calendar"></i>');
                var format = $datapicker.attr("data-format");
                var minViewMode = "days";
                if (format) {
                    if (format == "yyyy-mm") {
                        minViewMode = "months"
                    } else {
                        if (format == "yyyy") {
                            minViewMode = "years"
                        }
                    }
                }
                $datapicker.datepicker({
                    clearBtn: true,
                    autoclose: true,
                    todayBtn: true,
                    language: "zh-CN",
                    format: format ? format : "yyyy-mm-dd",
                    minViewMode: minViewMode
                });
                $("body").removeClass("modal-open")
            })
        }, initDateTimePicker: function ($container) {
            $('input[data-picker="date-time"]', $container).each(function () {
                var $datapicker = $(this);
                if ($datapicker.attr("data-picker-done")) {
                    return this
                }
                $datapicker.attr("data-picker-done", true);
                if ($datapicker.attr("readonly") || $datapicker.attr("disabled")) {
                    return
                }
                var timeInput = $datapicker.attr("data-toggle");
                $datapicker.wrap('<div class="input-icon right"></div>');
                $datapicker.before('<i class="fa fa-calendar"></i>');
                var format = $datapicker.attr("data-format");
                $datapicker.datetimepicker({
                    clearBtn: true,
                    autoclose: true,
                    todayBtn: true,
                    language: "zh-CN",
                    format: format ? format : "yyyy-mm-dd hh:ii",
                    minuteStep: 10
                });
                $("body").removeClass("modal-open")
            })
        }, initDateRangePicker: function ($container) {
            $('input[data-picker="date-range"]', $container).each(function () {
                var $datapicker = $(this);
                if ($datapicker.attr("data-picker-done")) {
                    return this
                }
                $datapicker.attr("data-picker-done", true);
                if ($datapicker.attr("readonly") || $datapicker.attr("disabled")) {
                    return
                }
                $datapicker.wrap('<div class="input-icon right"></div>');
                $datapicker.before('<i class="fa fa-calendar"></i>');
                var options = $.fn.daterangepicker.defaults;
                var dateScope = $datapicker.attr("data-date-scope");
                if (dateScope) {
                    if (dateScope == "afterNow") {
                        $datapicker.val(moment().format("YYYY-MM-DD") + " ~ " + moment().add("days", 29).format("YYYY-MM-DD"));
                        options.minDate = moment();
                        options.ranges = {
                            "今天": [moment(), moment()],
                            "未来一周": [moment(), moment().add("days", 6)],
                            "未来一月": [moment(), moment().add("days", 29)],
                            "未来一季度": [moment().subtract("days", 89), moment()],
                            "未来半年": [moment(), moment().add("days", 179)],
                            "未来一年": [moment(), moment().add("days", 364)]
                        }
                    } else {
                        if (dateScope == "beforeNow") {
                            $datapicker.val(moment().subtract("days", 29).format("YYYY-MM-DD") + " ~ " + moment().format("YYYY-MM-DD"));
                            options.maxDate = moment();
                            options.ranges = {
                                "今天": [moment(), moment()],
                                "昨天": [moment().subtract("days", 1), moment().subtract("days", 1)],
                                "本月": [moment().startOf("month"), moment().endOf("month")],
                                "上月": [moment().subtract("month", 1).startOf("month"), moment().subtract("month", 1).endOf("month")],
                                "最近一周": [moment().subtract("days", 6), moment()],
                                "最近一月": [moment().subtract("days", 29), moment()],
                                "最近一季度": [moment().subtract("days", 89), moment()],
                                "最近半年": [moment().subtract("days", 179), moment()],
                                "最近一年": [moment().subtract("days", 364), moment()]
                            }
                        } else {
                            alert("Undefined data-date-scope=" + dateScope)
                        }
                    }
                } else {
                    options.ranges = {
                        "今天": [moment(), moment()],
                        "昨天": [moment().subtract("days", 1), moment().subtract("days", 1)],
                        "最近一周": [moment().subtract("days", 6), moment()],
                        "未来一周": [moment(), moment().add("days", 6)],
                        "最近一月": [moment().subtract("days", 29), moment()],
                        "未来一月": [moment(), moment().add("days", 29)],
                        "最近一季度": [moment().subtract("days", 89), moment()],
                        "本月": [moment().startOf("month"), moment().endOf("month")],
                        "上月": [moment().subtract("month", 1).startOf("month"), moment().subtract("month", 1).endOf("month")]
                    }
                }
                $datapicker.daterangepicker(options, function (start, end) {
                    $datapicker.focus();
                    var $form = $datapicker.closest("form.form-search-auto");
                    if ($form.size() > 0) {
                        $form.submit()
                    }
                });
                $datapicker.off("focus");
                $("body").removeClass("modal-open")
            })
        }, initControlLabelTooltips: function ($container) {
            $(".control-label[data-tooltips]", $container).each(function () {
                var $el = $(this);
                if ($el.attr("data-tooltips-done")) {
                    return this
                }
                $el.attr("data-tooltips-done", true);
                var $tips = $('<span class="glyphicon glyphicon-exclamation-sign tooltipster" title="' + $el.attr("data-tooltips") + '"></span>').appendTo($el);
                var position = "top";
                if ($el.find("[data-rule-date]").length > 0) {
                    position = "right"
                }
                if ($el.attr("data-tooltipster-position")) {
                    position = $el.attr("data-tooltipster-position")
                }
                $tips.tooltipster({contentAsHTML: true, offsetY: 15, theme: "tooltipster-punk", position: position})
            })
        }, initTextareaMaxlength: function ($container) {
            $("textarea[maxlength]", $container).each(function () {
                var $el = $(this);
                if ($el.attr("data-maxlength-done")) {
                    return this
                }
                $el.attr("data-maxlength-done", true);
                $el.maxlength({limitReachedClass: "label label-danger", alwaysShow: true})
            })
        }, initTextareaHtmleditor: function ($container) {
            $("textarea[data-htmleditor='kindeditor']", $container).each(function () {
                var $kindeditor = $(this);
                if ($kindeditor.attr("data-htmleditor-done")) {
                    return this
                }
                $kindeditor.attr("data-htmleditor-done", true);
                var height = $kindeditor.attr("data-height");
                if (height == undefined) {
                    height = "500px"
                }
                KindEditor.create($kindeditor, {
                    allowFileManager: false,
                    width: "100%",
                    height: height,
                    minWidth: "200px",
                    minHeight: "60px",
                    afterBlur: function () {
                        this.sync()
                    }
                })
            })
        }, initImagePreivew: function ($container) {
            $("img[data-toggle='preview']", $container).each(function () {
                var $el = $(this);
                $el.mouseenter(function (e) {
                    this.t = this.title;
                    this.title = "";
                    var c = (this.t != "") ? "<br/>" + this.t : "";
                    var $preview = $("#image-preview");
                    if ($preview.size() == 0) {
                        var maxWidth = $(window).width() - 200;
                        $preview = $("<p id='image-preview'><img src='" + this.src + "' alt='Image preview'/>" + c + "</p>").appendTo($("body"));
                        $preview.css("top", "10px").css("left", "10px")
                    } else {
                        $preview.children("img").attr("src", this.src)
                    }
                    $preview.fadeIn("fast")
                })
            })
        }, initAjaxBeforeShow: function ($container) {
            if ($container == undefined) {
                $container = $("body")
            }
            if ($.fn.bootstrapSwitch) {
                $(".make-switch:not(.has-switch)")["bootstrapSwitch"]()
            }
            Page.initDynamicTable($container);
            Page.initDropdownSelect($container);
            Page.initMultiSelectDouble($container);
            Page.initSelect2Remote($container);
            Page.initSelect2Tags($container);
            Page.initSelect2($container);
            Page.initUploadSingleFile($container);
            Page.initDatePicker($container);
            Page.initDateTimePicker($container);
            Page.initDateRangePicker($container);
            Page.initControlLabelTooltips($container);
            Page.initTextareaMaxlength($container);
            Page.initTextareaHtmleditor($container);
            Page.initImagePreivew($container);
            $("form.form-search-auto label.btn", $container).on("click", function () {
                var $form = $(this).closest("form.form-search-auto");
                setTimeout(function () {
                    $form.submit()
                }, 100)
            });
            $("form.form-search-auto select", $container).on("change", function () {
                var $form = $(this).closest("form.form-search-auto");
                setTimeout(function () {
                    $form.submit()
                }, 100)
            });
            $("table.table-sorting", $container).each(function () {
                var $sortingTable = $(this);
                var $container = $sortingTable.closest(".ajax-get-container");
                var url = $container.attr("data-url");
                var sortName = Util.getParameterFromUrl(url, "sidx");
                if (sortName && sortName != "") {
                    var $highlightTh = $sortingTable.find("th[data-sorting-name='" + sortName + "']");
                    var sortDirection = Util.getParameterFromUrl(url, "sord");
                    if (sortDirection == "asc") {
                        $highlightTh.removeClass("sorting");
                        $highlightTh.addClass("sorting_asc")
                    } else {
                        if (sortDirection == "desc") {
                            $highlightTh.removeClass("sorting");
                            $highlightTh.addClass("sorting_desc")
                        } else {
                        }
                    }
                }
                $sortingTable.on("click", "th[data-sorting-name]", function () {
                    var $th = $(this);
                    var $container = $sortingTable.find("> tbody.ajax-get-container");
                    if ($container.size() == 0) {
                        $container = $sortingTable.closest(".ajax-get-container")
                    }
                    var url = $container.attr("data-url");
                    var desc = false;
                    if ($th.hasClass("sorting")) {
                        $th.removeClass("sorting");
                        $th.addClass("sorting_asc");
                        desc = "asc"
                    } else {
                        if ($th.hasClass("sorting_asc")) {
                            $th.removeClass("sorting_asc");
                            $th.addClass("sorting_desc");
                            desc = "desc"
                        } else {
                            $th.removeClass("sorting_desc");
                            $th.addClass("sorting")
                        }
                    }
                    url = Util.AddOrReplaceUrlParameter(url, "sidx", desc ? $th.attr("data-sorting-name") : "");
                    url = Util.AddOrReplaceUrlParameter(url, "sord", desc);
                    $sortingTable.removeAttr("data-scroll-loading").removeAttr("data-scroll-page");
                    $container.ajaxGetUrl(url)
                })
            });
            $('.nav > li > a[href="#tab-auto"]', $container).each(function () {
                var $link = $(this);
                var $nav = $link.closest(".nav");
                var idx = $nav.find("li:not(.tools)").index($link.parent("li"));
                var $tabPane = $nav.parent().children(".tab-content").find("div.tab-pane").eq(idx);
                var tabid = "__tab-" + new Date().getTime() + idx;
                $tabPane.attr("id", tabid);
                $link.attr("href", "#" + tabid)
            });
            $(".page-container .nav", $container).each(function () {
                var $nav = $(this);
                $nav.find("> li.active:first > a").click()
            });
            $("input.knob", $container).each(function () {
                $(this).knob({
                    dynamicDraw: true,
                    thickness: 0.2,
                    tickColorizeValues: true,
                    skin: "tron",
                    readOnly: true
                })
            })
        }, initAjaxAfterShow: function ($container) {
            if ($container == undefined) {
                $container = $("body")
            }
            var $navTabs = $(".nav-tabs", $container);
            if ($navTabs.size() > 0) {
                var active = null;
                var $lis = $navTabs.children("li:not(.tools):not(.dropdown)");
                var $actived = $lis.filter(".active");
                if ($actived.size() > 0) {
                    active = $lis.index($actived)
                }
                if (active == null || active == -1) {
                    var active = $navTabs.attr("data-active");
                    if (active && active != "") {
                        active = Number(active)
                    } else {
                        active = 0
                    }
                }
                $lis.filter(":eq(" + active + ")").find("> a").click()
            }
            $("button:not([type])", $container).each(function () {
                $(this).attr("type", "button")
            });
            $('div[data-toggle="ajaxify"]', $container).each(function () {
                $(this).ajaxGetUrl($(this).attr("data-url"))
            })
        }, doSomeStuff: function () {
            myFunc()
        }
    }
}();