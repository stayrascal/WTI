var App = function () {
    var b = false;
    var g = false;
    var e = false;
    var a = false;
    var k = function () {
        if ($("body").css("direction") === "rtl") {
            b = true
        }
        g = !!navigator.userAgent.match(/MSIE 8.0/);
        e = !!navigator.userAgent.match(/MSIE 9.0/);
        a = !!navigator.userAgent.match(/MSIE 10.0/);
        if (a) {
            jQuery("html").addClass("ie10")
        }
    };

    function n() {
        if (g || e) {
            jQuery("input[placeholder]:not(.placeholder-no-fix), textarea[placeholder]:not(.placeholder-no-fix)").each(function () {
                var p = jQuery(this);
                if (p.val() == "" && p.attr("placeholder") != "") {
                    p.addClass("placeholder").val(p.attr("placeholder"))
                }
                p.focus(function () {
                    if (p.val() == p.attr("placeholder")) {
                        p.val("")
                    }
                });
                p.blur(function () {
                    if (p.val() == "" || p.val() == p.attr("placeholder")) {
                        p.val(p.attr("placeholder"))
                    }
                })
            })
        }
    }

    function m() {
        jQuery(".carousel").carousel({interval: 15000, pause: "hover"});
        jQuery(".tooltips").tooltip();
        jQuery(".popovers").popover()
    }

    function l() {
        jQuery(".top").click(function () {
            jQuery("html,body").animate({scrollTop: jQuery("body").offset().top}, "slow")
        })
    }

    function j() {
        $(".search-btn").click(function () {
            if ($(".search-btn").hasClass("show-search-icon")) {
                $(".search-box").fadeOut(300);
                $(".search-btn").removeClass("show-search-icon")
            } else {
                $(".search-box").fadeIn(300);
                $(".search-btn").addClass("show-search-icon")
            }
        })
    }

    function i() {
        if (!jQuery().uniform) {
            return
        }
        var p = $("input[type=checkbox]:not(.toggle), input[type=radio]:not(.toggle, .star)");
        if (p.size() > 0) {
            p.each(function () {
                if ($(this).parents(".checker").size() == 0) {
                    $(this).show();
                    $(this).uniform()
                }
            })
        }
    }

    var d = function () {
        if (!jQuery.fancybox) {
            return
        }
        if (jQuery(".fancybox-button").size() > 0) {
            jQuery(".fancybox-button").fancybox({
                groupAttr: "data-rel",
                prevEffect: "none",
                nextEffect: "none",
                closeBtn: true,
                helpers: {title: {type: "inside"}}
            });
            $(".fancybox-video").fancybox({type: "iframe"})
        }
    };
    var c = function () {
        if (!window.addEventListener) {
            window.attachEvent("scroll", function (x) {
                if ($("body").hasClass("page-header-fixed") === false) {
                    return
                }
                if (!v) {
                    v = true;
                    setTimeout(u, 250)
                }
            })
        } else {
            window.addEventListener("scroll", function (x) {
                if ($("body").hasClass("page-header-fixed") === false) {
                    return
                }
                if (!v) {
                    v = true;
                    setTimeout(u, 250)
                }
            }, false)
        }
        var q = document.documentElement, w = $(".navbar-inner"), r = $(".front-header"), s = $(".slider-main"), v = false, p = 300;

        function u() {
            var x = t();
            if (x >= p) {
                r.addClass("front-header-shrink");
                w.addClass("navbar-inner-shrink");
                $("#logoimg").attr("width", "120px");
                $("#logoimg").attr("height", "18px")
            } else {
                r.removeClass("front-header-shrink");
                w.removeClass("navbar-inner-shrink");
                $("#logoimg").attr("width", "142px");
                $("#logoimg").attr("height", "21px")
            }
            v = false
        }

        function t() {
            return window.pageYOffset || q.scrollTop
        }
    };
    var o = function () {
        var p = $(".color-panel");
        var q = function (r) {
            $("#style_color").attr("href", WEB_ROOT + "/assets/w/css/themes/" + r + (b ? "-rtl" : "") + ".css");
            $("#logoimg").attr("src", "assets/img/logo_" + r + ".png");
            $("#rev-hint1").attr("src", "assets/img/sliders/revolution/hint1-" + r + ".png");
            $("#rev-hint2").attr("src", "assets/img/sliders/revolution/hint2-" + r + ".png")
        };
        $(".icon-color", p).click(function () {
            $(".color-mode").show();
            $(".icon-color-close").show()
        });
        $(".icon-color-close", p).click(function () {
            $(".color-mode").hide();
            $(".icon-color-close").hide()
        });
        $("li", p).click(function () {
            var r = $(this).attr("data-style");
            q(r);
            $(".inline li", p).removeClass("current");
            $(this).addClass("current")
        });
        $(".header-option", p).change(function () {
            if ($(".header-option").val() == "fixed") {
                $("body").addClass("page-header-fixed");
                $(".header").addClass("navbar-fixed-top").removeClass("navbar-static-top");
                App.scrollTop()
            } else {
                if ($(".header-option").val() == "default") {
                    $("body").removeClass("page-header-fixed");
                    $(".header").addClass("navbar-static-top").removeClass("navbar-fixed-top");
                    $(".navbar-inner").removeClass("navbar-inner-shrink");
                    $(".front-header").removeClass("front-header-shrink");
                    App.scrollTop()
                }
            }
        })
    };
    return {
        init: function () {
            k();
            m();
            n();
            l();
            j();
            o();
            d();
            c()
        }, blockUI: function (p, q) {
            var p = jQuery(p);
            if (p.height() <= 400) {
                q = true
            }
            p.block({
                message: '<img src="' + WEB_ROOT + '/assets/img/ajax-loading.gif" align="">',
                centerY: q != undefined ? q : true,
                css: {top: "10%", border: "none", padding: "2px", backgroundColor: "none"},
                overlayCSS: {backgroundColor: "#000", opacity: 0.05, cursor: "wait"}
            })
        }, unblockUI: function (q, p) {
            jQuery(q).unblock({
                onUnblock: function () {
                    jQuery(q).css("position", "");
                    jQuery(q).css("zoom", "")
                }
            })
        }, isIE8: function () {
            return g
        }, isIE9: function () {
            return e
        }, initUniform: function (p) {
            if (p) {
                jQuery(p).each(function () {
                    if ($(this).parents(".checker").size() == 0) {
                        $(this).show();
                        $(this).uniform()
                    }
                })
            } else {
                i()
            }
        }, initBxSlider: function () {
            $(".bxslider").show();
            $(".bxslider").bxSlider({
                minSlides: 3,
                maxSlides: 3,
                slideWidth: 360,
                slideMargin: 10,
                moveSlides: 1,
                responsive: true,
            });
            $(".bxslider1").show();
            $(".bxslider1").bxSlider({
                minSlides: 6,
                maxSlides: 6,
                slideWidth: 360,
                slideMargin: 2,
                moveSlides: 1,
                responsive: true,
            })
        }, scrollTo: function (q, p) {
            pos = q ? q.offset().top : 0;
            jQuery("html,body").animate({scrollTop: pos + (p ? p : 0)}, "slow")
        }, scrollTop: function () {
            App.scrollTo()
        }, gridOption1: function () {
            $(function () {
                $(".grid-v1").mixitup()
            })
        }
    };
    var f = function () {
        var p;
        jQuery("body").on("click", ".accordion.scrollable .accordion-toggle", function () {
            p = jQuery(this)
        });
        jQuery("body").on("show.bs.collapse", ".accordion.scrollable", function () {
            jQuery("html,body").animate({scrollTop: p.offset().top - 150}, "slow")
        })
    };
    var h = function () {
        $("body").on("shown.bs.tab", ".nav.nav-tabs", function () {
            handleSidebarAndContentHeight()
        });
        if (location.hash) {
            var p = location.hash.substr(1);
            $('a[href="#' + p + '"]').click()
        }
    }
}();