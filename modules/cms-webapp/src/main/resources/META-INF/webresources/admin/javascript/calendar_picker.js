/*
 *      dyn/calendar
 *
 *      Copyright 2007 Azer Ko�ulu <http://azer.kodfabrik.com>
 *
 * 		24.09.2007
 * 		update1: 23.02.2008 15:15:59
 *
 *      This program is free software; you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation; either version 2 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program; if not, write to the Free Software
 *      Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 *      MA 02110-1301, USA.
 */


/*
 *      Some parts of this code has been tweaked to better suit the need of our CMS.
 *      Therefore custom date formating is breaking. Only this format is supported : dd.mm.yyyy
 *
 *      Features added are:
 *
 *        * Support for first day of week.
 *        * Open the month and year described in the input field.
 *
 *      tan@enonic.com
 */

var g_cal_firstDayOfWeek = 1;

var calendar = {

    a: { format:"mm/dd/yyyy", events:{}, lang: "cms", cursor:[] },
    lang: { "cms":
            [
                [
                    "%calJanuary%",
                    "%calFebruary%",
                    "%calMarch%",
                    "%calApril%",
                    "%calMay%",
                    "%calJune%",
                    "%calJuli%",
                    "%calAugust%",
                    "%calSeptember%",
                    "%calOctober%",
                    "%calNovember%",
                    "%calDecember%"
                ],
                [
                    "%calSunday%",
                    "%calMonday%",
                    "%calTuesday%",
                    "%calWedensday%",
                    "%calThursday%",
                    "%calFriday%",
                    "%calSaturday%"
                ],
                [
                    "%calSun%",
                    "%calMon%",
                    "%calTue%",
                    "%calWed%",
                    "%calThu%",
                    "%calFri%",
                    "%calSat%"
                ]
            ]
    },
    elm:{},
    init: function(input, format)
    {
        with (calendar)
        {
            lib.p(window);
            elm.container = window.make("div", { att:{ id:"calendar" } });

            elm.a = elm.container.make("table", { att: { cellpadding:'0', cellspacing:'0' }, css:{ width:"200px", background:"#fff", font:"11px 'Trebuchet MS',Arial,Sans",  color:"#666",  border:"1px solid #2D2A28", padding:"0" } });
            elm.b = elm.a.make("thead");
            elm.c = elm.b.make("tr");
            elm.c = elm.c.make("td", { att:{ colspan:"7", "class":"navigate" }, css:{ background:"#2D2A28", color:"#fff", padding:"4px" } });
            elm.arrow_0 = elm.c.make("a::&lsaquo;", { att:{ href:"javascript:calendar.doo.pickMonth(0);", "class":"arrow left" } });
            elm.arrow_2 = elm.c.make("a::&rsaquo;", { att:{ "class":"arrow", "href":"javascript:calendar.doo.pickMonth(1);" } });
            elm.arrow_1 = elm.c.make("a::&lsaquo;&lsaquo;", { att:{ href:"javascript:calendar.doo.pickYear(0);", "class":"arrow doubleLeft" } });
            elm.arrow_3 = elm.c.make("a::&rsaquo;&rsaquo;", { att:{ "class":"arrow doubleRight", "href":"javascript:calendar.doo.pickYear(1);" } });
            elm.c = elm.c.make("div", { css:{ "textAlign":"center", "fontWeight":"bold" } });

            elm.d = elm.b.make("tr", { att:{ "class":"top" } });

			// CMS specific. Render the day columns according to startDay.
            var day = g_cal_firstDayOfWeek;
            for (var i = 0; i < lang[a.lang][2].length; i++) {
                elm.d.make("th::" + lang[a.lang][2][day]);
                day++;
                if (day == lang[a.lang][2].length)
                    day = 0;

            }

            elm.e = [];

            if (Boolean(window["calendar_events"]))
                a.events = window["calendar_events"];

            a.date = new Date();
            a.date = a.date.get();

            a.cursor = new Date();
            a.cursor = [a.cursor.getMonth(),a.cursor.getFullYear()];
            doo.setMonth();

            document.body[lib.n.ie ? "attachEvent" : "addEventListener"]((lib.n.ie ? "on" : "") + "mousedown", doo.listen, 1);
        }
    },
    doo: {
        listen: function(fact)
        {

            with (calendar)
            {
                var close = 1;
                var target = fact[lib.n.ie ? "srcElement" : "target"];

                if (elm.container.style.display != "block")return;
                while (target != document.body && target != elm.container)
                {

                    if (target == elm.container || target == elm.a || target == elm.b || target == elm.c)
                    {
                        close = 0;
                        break;
                    }
                    target = target.parentNode;
                }

                if (close == 0)return;
                elm.container.style.display = "none";
            }
        },
        display: function(input, format, calendarButton)
        {
            with (calendar)
            {
                calendar.doo.update(input);
                elm.input = input;
                a.format = typeof format != "undefined" ? format : "mm/dd/yyyy";
                elm.container.style.display = "block";
                elm.container.style.top = lib.i(elm.input).y + "px";
                elm.container.style.left = lib.i(elm.input).x + elm.input.offsetWidth + "px";
                if (document.all && elm.container.lastChild.tagName != 'IFRAME') {
                    var _if = document.createElement('iframe');
                    elm.container.appendChild(_if);
                }

                // Key navigation fix.
                // When the calendar is opened the calendar icon/button has focus.
                // If the user hits the tab key the calendar will not be closed.
                calendarButton[lib.n.ie ? "attachEvent" : "addEventListener"]((lib.n.ie ? "on" : "") + "keydown", function(e) {
                    if ( !e ) var e = window.event;
                    if( e.keyCode == 9 ) calendar.doo.close();
                }, 1);

            }


        },

        update : function(input)
        {
            with (calendar)
            {
                var inputValue = input.value.toString();
                var validDate = /^((((0?[1-9]|[12]\d|3[01])[\.](0?[13578]|1[02])[\.]((1[6-9]|[2-9]\d)?\d{2}))|((0?[1-9]|[12]\d|30)[\.](0?[13456789]|1[012])[\.]((1[6-9]|[2-9]\d)?\d{2}))|((0?[1-9]|1\d|2[0-8])[\.]0?2[\.]((1[6-9]|[2-9]\d)?\d{2}))|(29[\.]0?2[\.]((1[6-9]|[2-9]\d)?(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00)|00)))|(((0[1-9]|[12]\d|3[01])(0[13578]|1[02])((1[6-9]|[2-9]\d)?\d{2}))|((0[1-9]|[12]\d|30)(0[13456789]|1[012])((1[6-9]|[2-9]\d)?\d{2}))|((0[1-9]|1\d|2[0-8])02((1[6-9]|[2-9]\d)?\d{2}))|(2902((1[6-9]|[2-9]\d)?(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00)|00))))$/;
                if (inputValue == '' || inputValue.length == 10 && validDate.test(inputValue)) {

                    var d = new Date();

                    // Use NOW if there is no valid date in the input field.
                    if (inputValue == '') {
                        var day = d.getDay().toString();
                        var month = d.getMonth() + 1;
                        var year = d.getFullYear();

                        // Add leading zeros.
                        day = (day < 10 ? '0' + day : day);
                        month = (month < 10 ? '0' + month : month);
                    }

                    var calendarYear = a.cursor[1];

                    var dates = inputValue.split('.');
                    var mm = dates[1] || month.toString();
                    var yyyy = parseInt(dates[2]) || parseInt(year);
                    var monthNum = mm.substring(0, 1) == 0 ? mm.substring(1, 2) : mm;

                    if (a.cursor[1] == yyyy && ((a.cursor[0] + 1) == monthNum)) return;

                    var diffY = (yyyy - calendarYear);
                    var absY = Math.abs(diffY);
                    var isAbsoluteY = diffY < 0;

                    if (!isAbsoluteY)
                        a.cursor[1] = a.cursor[1] + absY;
                    else
                        a.cursor[1] = a.cursor[1] - absY;

                    var diffM = (monthNum - (a.cursor[0] + 1));
                    var absM = Math.abs(diffM);
                    var isAbsoluteM = diffM < 0;

                    if (!isAbsoluteM)
                        a.cursor[0] = a.cursor[0] + absM;
                    else
                        a.cursor[0] = a.cursor[0] - absM;

                    doo.setMonth();
                } 
            }
        },
        
        close: function()
        {
            if (document.getElementById('calendar'))
                document.getElementById('calendar').style.display = 'none';
        },

        pickDay: function(fact)
        {
            with (calendar)
            {
                var target = fact[lib.n.ie ? "srcElement" : "target"];
                var day = doo.fixZero(target.innerHTML);

                elm.input.value = a.format.replace("dd", day).replace("mm", doo.fixZero(a.cursor[0] + 1)).replace("yyyy", a.cursor[1]).replace("D", lang[a.lang][1][parseInt(target.getAttribute("weekDay"))]).replace("M", lang[a.lang][0][a.cursor[0]]);
                elm.container.style.display = "none";

                elm.input.focus();
            }
        },
        pickMonth: function(value)
        {
            with (calendar)
            {
                a.cursor[0] += (value = value ? 1 : -1);
                if (a.cursor[0] == -1)
                    a.cursor = [11,a.cursor[1] - 1];
                else if (a.cursor[0] == 12)
                    a.cursor = [0,a.cursor[1] + 1];
                doo.setMonth();
            }
        },
        pickYear: function(value)
        {
            with (calendar)
            {
                a.cursor[1] += (value = value ? 1 : -1);
                doo.setMonth();
            }
        },
        setMonth: function()
        {
            with (calendar)
            {
                var firstDay = new Date();
                firstDay.setFullYear(a.cursor[1], a.cursor[0], 1);
                firstDay = firstDay.get();

                var lastDay = new Date();
                lastDay.setFullYear(a.cursor[1], a.cursor[0], firstDay.month.length);
                lastDay = lastDay.get();

                var prvMonth = new Date();
                prvMonth.setFullYear(a.cursor[1], a.cursor[0] - 1);
                prvMonth = prvMonth.get();

                var length = firstDay.month.length + firstDay.day.ofWeek + (6 - lastDay.day.ofWeek);
                elm.c.innerHTML = lang[a.lang][0][firstDay.month.ofYear] + " " + a.cursor[1];

                for (x in elm.e)
                    elm.b.removeChild(elm.e[x]);
                elm.e = [];
                elm.e.push(elm.b.make("tr"));
                var x = -1;
                for (var i = 0; i < length; i++)
                {

                    day = i + 1 - firstDay.day.ofWeek;

                    var details = new Date();
                    details.setFullYear(a.cursor[1], a.cursor[0], parseInt(day));
                    details = details.get();

                    isPrvMonth = i < firstDay.day.ofWeek;
                    isNxtMonth = day > firstDay.month.length;

                    if (x == 6)elm.e.push(elm.b.make("tr"));

                    elm["cell_" + i] = elm.e[elm.e.length - 1].make("td::" + ((isPrvMonth ? prvMonth.month.length - (firstDay.day.ofWeek - i - 1) : (isNxtMonth == 0 ? (day) : (i - firstDay.day.ofWeek - firstDay.month.length + 1)))), { att: { weekDay:details.day.ofWeek, "class":(isPrvMonth || isNxtMonth ? "cell lightColor" : "cell") + (day == a.date.day.ofMonth && a.cursor[0] == a.date.month.ofYear && a.date.year == a.cursor[1] ? " today" : "") }, on:(isPrvMonth || isNxtMonth ? {} : { "mouseover":doo.mouseover, "click":doo.pickDay }) });

                    var date = doo.fixZero(firstDay.month.ofYear + (isPrvMonth ? 0 : (isNxtMonth ? 2 : 1))) + "." + doo.fixZero(day) + "." + doo.fixZero(firstDay.year);
                    if (Boolean(a.events[date]))
                    {

                        var data = a.events[date];
                        for (p in data)
                            if (p == "css")
                                for (k in data[p])
                                    elm["cell_" + i]["style"][k] = data[p][k];
                            else
                                elm["cell_" + i].on(p, data[p]);
                    }
                    x = x < 6 ? (x + 1) : 0;
                }
            }
        },
        mouseover: function(fact)
        {
            with (calendar)
            {
                var target = fact[lib.n.ie ? "srcElement" : "target"];
                target.att("class", "mouseover");
                target.onmouseout = function() {
                    target.att("class", "mouseover", 1)
                };
            }
        },
        fixZero: function(text)
        {
            return (String(text).length > 1 ? text : ("0" + String(text)));
        }
    },
    lib: {
        n: { ie:/MSIE/i.test(navigator.userAgent), ie6:/MSIE 6/i.test(navigator.userAgent), ie7:/MSIE 7/i.test(navigator.userAgent), op:/Opera/i.test(navigator.userAgent), ff:/firefox/i.test(navigator.userAgent) },
        e: function(att, val, tag)
        {
            return ((att == "id" ? document.getElementById(val) : (typeof att != "string" && typeof val != "string" && typeof tag == "string" ? document.getElementsByTagName(tag) : function() {

                var data = [];
                var tag = document.getElementsByTagName(typeof tag == "string" ? tag : "*");
                for (x in tag)
                    if (((parseInt(x)) >= 0 || calendar.lib.n.ie) && typeof tag[x] == 'object' && tag[x].getAttribute(att) && (typeof val != "string" || tag[x].getAttribute(att) == val))
                        data.push(tag[x]);
                return data;

            }())));
        },
        p: function(node)
        {
            node.make = function(tag, body, html)
            {
                var element = document.createElement(typeof tag == "string" ? tag.split("::")[0] : "div");
                if (typeof body == "object")
                    for (y in body)
                        for (x in body[y])
                            if (y == "css")
                                element.style[(x == "cssFloat" && calendar.lib.n.ie ? "styleFloat" : x)] = body[y][x];
                            else if (y == "att" || y == "on")
                                element[y == "att" ? "setAttribute" : (calendar.lib.n.ie ? "attachEvent" : "addEventListener")](y == "att" ? (x == "class" && calendar.lib.n.ie ? "className" : x) : ((calendar.lib.n.ie ? "on" : "") + x), body[y][x], false);

                try {
                    element.innerHTML = typeof tag == "string" && tag.split("::").length > 1 ? tag.split("::")[1] : (typeof html == "undefined" ? "" : html);
                }
                catch (e) {
                }
                (node == document || node == window ? document.body : node).appendChild(element);
                return calendar.lib.p(element);
            };

            node.css = function(property)
            {
                return (calendar.lib.n.ie == 0 ? document.defaultView.getComputedStyle(node, null) : node.currentStyle)[property];
            };

            node.on = function(ev, fu)
            {
                return node[calendar.lib.n.ie ? "attachEvent" : "addEventListener"]((calendar.lib.n.ie ? "on" : "") + ev, fu, 1);
            }

            node.att = function(att, val, remove)
            {
                if (typeof node.getAttribute == "undefined")return 0;

                var ieFix = { "class":"className" };

                if (typeof val != "undefined" && typeof remove == "undefined") node.setAttribute(calendar.lib.n.ie ? ieFix[att] : att, att == "class" ? node.att(calendar.lib.n.ie ? ieFix[att] : att) + ' ' + val : val);
                else if (typeof val != "undefined" && typeof remove != "undefined") node.setAttribute(calendar.lib.n.ie ? ieFix[att] : att, node.att(calendar.lib.n.ie ? ieFix[att] : att).replace(val));

                return node.getAttribute(att);
            }

            return node;
        },
        i: function(elm)
        {
            var dim = [0,0];
            while (elm && document.firstChild && elm.nodeName != 'BODY')
            {
                dim = [dim[0] + elm.offsetLeft,dim[1] + elm.offsetTop];
                elm = elm.offsetParent;
            }
            return { x:dim[0], y:dim[1] };
        }
    }
}

Date.prototype.get = function() {
    return { hour:{ am:this.getHours(), pm:Math.abs(12 - this.getHours()) }, minute:this.getMinutes(), day:{  "ofWeek":this.getDay() - g_cal_firstDayOfWeek, "ofMonth":this.getDate() }, month:{ "ofYear":this.getMonth(), "length":(this.getMonth() == 1 ? (this.getFullYear() % 4 == 0 ? 29 : 28) : (this.getMonth() < 7 ? (this.getMonth() % 2 == 0 ? 31 : 30) : (this.getMonth() % 2 == 0 ? 30 : 31))) }, year:this.getFullYear() }
}

window[calendar.lib.n.ie ? "attachEvent" : "addEventListener"]((calendar.lib.n.ie ? "on" : "") + "load", calendar.init, 1);
