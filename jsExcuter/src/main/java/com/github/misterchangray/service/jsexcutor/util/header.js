///////////////////////////// 全局常量定义
//////////////////////////////////////////////////////////////////////////////////////////////////////
const VER = "0.0.1";
const DEFAULT_KLINE_SIZE="200";
const virtual = false;
const ORDER_STATE_PENDING=0;//	未完成	0
const ORDER_STATE_CLOSED=1;//	已经完成	1
const ORDER_STATE_CANCELED=2;//	已经取消	2
const ORDER_TYPE_BUY=0;//	买	0
const ORDER_TYPE_SELL=1;//	卖	1
const PD_LONG=0;//	多头仓位(CTP用closebuy_today平仓)	0
const PD_SHORT=1;//	空头仓位(CTP用closesell_today平仓)	1
const PD_LONG_YD=2;//	昨日多头仓位(用closebuy平)	2
const PD_SHORT_YD=3;//	昨日空头仓位(用closesell平)	3
const PERIOD_M1="PERIOD_M1";
const PERIOD_M5="PERIOD_M5";
const PERIOD_M15="PERIOD_M15";
const PERIOD_M30="PERIOD_M30";
const PERIOD_H1="PERIOD_H1";
const PERIOD_D1="PERIOD_D1";
const __Configs=[];

const __Global = function(pro, val) {
    if(1 == arguments.length) {
        return JSON.parse(__utils.global.get(pro));
    } else {
        if(undefined != val) {
            __utils.global.put(pro, JSON.stringify(val))
        } else {
            __utils.global.remove(pro)
        }
    }
};


const initExchanges = function() {
    var configs = __Configs;
    if(!configs) throw "没有设置平台配置;使用 Configs() 函数来配置";
    if(configs && Array.isArray(configs) && 0 == configs.length) throw "没有设置平台配置;使用 Configs() 函数来配置";

    for(var i in configs) {
        var t = new ExchangeFatory(configs[i]);
        exchanges.push(t);
    }
    exchange = exchanges[0];
};
const __init = function() {
    initExchanges();
    return true;
};

const Configs = function(p) {
    if(Array.isArray(p)) {
        if(p && Array.isArray(p)) {
            for(var i in p) {
                __Configs.push(p[i]);
            }
        }
    } else {
        throw "传入得参数应该为数组";
    }
};

const __Stop = function() {
    throw Error("ProgramExit");
};

///////////////////////////// 交易所函数实现
//////////////////////////////////////////////////////////////////////////////////////////////////////
const ExchangeParent = function() {
    ////////////////////////////////////////////////////市场信息
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    this.GetTicker = function() {
        return parse(__utils.GetTicker(this.Configs));
    };
    this.GetDepth = function() {
        return parse(__utils.GetDepth(this.Configs));
    };
    this.GetTrades = function() {
        return parse(__utils.GetTrades(this.Configs));
    };
    this.GetRecords = function(Period, size) {
        Period = Period || this.Configs["__DefaultPeriod"];
        var covert = {
            "PERIOD_M1":"1min",
            "PERIOD_M5":"5min",
            "PERIOD_M15":"15min",
            "PERIOD_M30":"30min",
            "PERIOD_H1":"1hour",
            "PERIOD_H2":"2hour",
            "PERIOD_D1":"day"
        };
        Period = covert[Period];
        size = size || DEFAULT_KLINE_SIZE;
        return parse(__utils.GetRecords(Period, size, this.Configs));
    };
    ///////////////////////////////////////////////////交易操作
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    this.Buy = function(Price, Amount) {
        if(Price == "-1" || Price == -1) Price = null;
        if(Amount == "-1" || Amount == -1) Amount = null;
        return __utils.Buy(Price, Amount, this.Configs);
    };
    this.Sell = function(Price, Amount) {
        if(Price == "-1" || Price == -1) Price = null;
        if(Amount == "-1" || Amount == -1) Amount = null;
        return __utils.Sell(Price, Amount, this.Configs);
    };
    this.CancelOrder = function(orderId) {
        return __utils.CancelOrder(orderId, this.Configs);
    };
    this.GetOrder = function(orderId) {
        return __utils.GetOrder(orderId);
    };
    this.GetOrders = function() {
        return __utils.GetOrders();
    };
    this.SetPrecision = function(PricePrecision, AmountPrecision) {
        var tmp = PricePrecision + ";" + AmountPrecision;
        this.Configs["__Precision"] = tmp;
        return tmp;
    };
    this.IO = function(type, httpMethod, resource, params) {
        var method = {"GET":true, "POST":true, "PUT":true, "DELETE":true};
        if(type == "api" && method[httpMethod]) {
            //调用交易所其它功能接口
            var data = {};
            if(null != params) {
                var tmp = params.split(/[=&]/);
                for(var i=0,j=tmp.length; i<j; i++) {
                    if(tmp[i] && tmp[i+1]) data[tmp[i]] = tmp[++i];
                }
            }
            return parse(__utils.IO(httpMethod, resource, data, this.Configs));
        }
    };

    ///////////////////////////////////////////////////账户信息
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    this.GetAccount = function() {
        return parse(__utils.GetAccount(this.Configs));
    };
    this.GetName = function() {
        return this.Configs["__ORG"];
    };
    this.GetLabel = function() {
        return this.Configs["__COIN"];
    };
    this.GetCurrency = function() {
        return this.Configs["__COIN"];
    };
    ///////////////////////////////////////////////////期货交易
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    this.GetPosition = function() {
        return parse(__utils.GetPosition(this.Configs));
    };
    this.SetMarginLevel = function(MarginLevel) {
        this.Configs["__MarginLevel"] = MarginLevel;
        return MarginLevel;
    };
    this.SetDirection = function(Direction) {
        var map = {"buy":true, "sell":true, "closebuy":true, "closesell":true};
        if(Direction && map[Direction.toLowerCase()]) {
            this.Configs["__Direction"] = Direction;
            return Direction;
        }
    };
    this.SetContractType = function(ContractType) {
        this.Configs["__ContractType"] = ContractType;
        return ContractType;
    }

    ///////////////////////////////////////////////////其他函数
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

};
const ExchangeFatory = function(config) {
    this.Configs = config;
    ExchangeParent.call(this);
};
const exchanges = [];
var exchange = null;

const Log = function() {
    var covert = ["Info", "Warn", "Error", "Reboot", "Buy", "Sell", "Profit"];
    var level = undefined;
    if(-1 != covert.indexOf(arguments[0])) {
        level = arguments[0];
    }
    var s = "";
    for(var i in arguments) {
        if(level && i > 0) {
            s += " " + JSON.stringify(arguments[i]);
        } else if(!level) {
            s += " " + JSON.stringify(arguments[i]);
        }
    }

    if(undefined == level) level = "Info";
    __utils.log(level, s);
};


const LogReset = function(t) {
    __utils.logReset();
};
const LogVacuum = function() {
    Log("目前不支持此设置,LogVacuum ");
};
const LogProfit = function(t) {
    var cache = _G("cache") || {};
    var Profit = cache["Profit"] || [];
	Profit.push({
        Time : new Date().getTime(),
        Profit : t
    });
	_G("cache", cache);
};

const EnableLog = function() {
   
};
const LogStatus = function(t) {
    var cache = _G("cache") || {};
    cache["LogStatus"] = t;
    _G("cache", cache);
};
const LogProfitReset = function(t) {
    var cache = _G("cache") || {};
    cache["Profit"] = [];
    _G("cache", cache);
};
const Sleep = function(t){
    try {
        __utils.sleep(t)
    } catch(e) {
        throw  Error("ProgramExit");
    }
};

const _G = function(pro, val) {
	if(1 == arguments.length) {
		return JSON.parse(__utils.Storage(pro, null, "get"));
	} else {
		if(undefined !=  val) {
            __utils.Storage(pro, JSON.stringify(val), "set")
		} else {
            __utils.Storage(pro, null, "remove")
		}
	}
};
const _D =  function(timestamp, pattern) {
    pattern = pattern || "yyyy-MM-dd";
    timestamp = timestamp || new Date().getTime();

    var tmp = new Date(timestamp);
    var o = {
        "M+": tmp.getMonth() + 1,
        "d+": tmp.getDate(),
        "h+": tmp.getHours(),
        "m+": tmp.getMinutes(),
        "s+": tmp.getSeconds(),
        "q+": Math.floor((tmp.getMonth() + 3) / 3),
        "S": tmp.getMilliseconds()
    };
    if (/(y+)/.test(pattern)) {
        pattern = pattern.replace(RegExp.$1, (tmp.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(pattern)) {
            pattern = pattern.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return pattern;
};
const _N = function(num, precision) {
	var tmp = Math.pow(10, precision || 0);
	return parseInt((num * tmp)) /  tmp;
};

const _C = function() {
    var arg0 = arguments[0];
    var fn;
    var params = [];

    for(var i in arguments) {
        params.push(arguments[i]);
    }
    if('string' != typeof(arg0)) {
       if(arg0 > 100) throw "retry max for _C 100";
        params[0] = (arg0 + 1);
        fn = arguments[1];
    } else {
        fn = arg0;
        params.splice(0,0,0);
    }
    if(fn && "string" == typeof fn) {
        var tmp = params.slice(2);
        var res = eval(fn).apply(eval(fn.split(".")[0]), tmp);
        if(undefined !=  res) {
            return res;
        } else {
            Sleep(1000);
            return  _C.apply(null, params);
        }
    }
};




const GetCommand = function() {
	var cmd = _G("__Command");
	if(cmd) {
		return "cmd:" + cmd;
	}
	return;
};

const parse = function(t) {
	if('[object String]' == Object.prototype.toString.call(t)) {
		try {
			return JSON.parse(t);
		} catch(e) {
			return null;
		}
	}
	return t;
};
const IsVirtual = function() {
	return virtual;
};

const Version = function() {
	return VER;
};

const GetPid = function() {
	return null;
};


///////////////////////////////////////////////////其他全局函数
////////////////////////////////////////////////////////////////////////////////////////////////////////////////


const ChartFactory = function() {
    this.id = new Date().getTime();
    this.opts;
    this.add = function() {
        try {
            if(this.opts) {
                if(1 == arguments.length && Array.isArray(arguments[0])) {
                    var arg = arguments[0];
                    this.opts.series[arg[0]].data.push(arg[1]);
                }
            } else {
                Log("please init chart options in you use");
            }
        } catch (e) {
            Log(e.toString());
        }

    };
    this.reset = function() {
        try{
            for(var i in this.opts.series) {
                this.opts.series[i].data = [];
            }
        } catch (e) {
            Log(e.toString());
        }
    };
    this.update = function() {


        try {
            var cache = _G("cache") || {};
            var charts = cache["Charts"] || [];
            for(var i in charts) {
                if(charts[i].id == this.id) {
                    charts[i].opts = this.opts;
                }
            }
            _G("cache", cache);
        } catch (e) {
            Log(e.toString());
        }
    }
};

const Chart = function() {
    var arg = arguments;
    if(1 == arg.length) {

        var cache = _G("cache") || {};
        cache["Charts"]  =  [];

        var chart = new ChartFactory();
        chart.opts = arg[0];
        cache["Charts"].push(chart);

        _G("cache", cache);
        return chart;
    }
};
const Mail = function() {
    Log("目前不支持此设置,Mail ");
};
const SetErrorFilter = function() {
    Log("目前不支持此设置,SetErrorFilter ");
};
const GetLastError = function() {
    Log("目前不支持此设置,GetLastError ");
};
const Dial = function() {
    Log("目前不支持此设置,Dial ");
};
const HttpQuery = function() {
    Log("目前不支持此设置,SetTimeout ");
};
const Hash = function() {
    Log("目前不支持此设置,Hash ");
};
const HMAC = function() {
    Log("目前不支持此设置,HMAC ");
};
const UnixNano = function() {
    Log("目前不支持此设置,UnixNano ");
};
const Unix = function() {
    Log("目前不支持此设置,Unix ");
};
const GetOS = function() {
    Log("目前不支持此设置,GetOS ");
};
const MD5 = function() {
    Log("目前不支持此设置,MD5 ");
};



const SetProxy = function() {
    Log("目前不支持此设置,SetProxy ");
};
const SetTimeout = function() {
    Log("目前不支持此设置,SetTimeout ");
};

const Cross = function(arr1, arr2) {           
	// 返回上穿的周期数. 正数为上穿周数, 负数表示下穿的周数, 0指当前价格一样
 // 参数个数为2个，从参数名可以看出，这两个 参数应该都是 数组类型，数组就
                                            // 好比是 在X轴为 数组索引值，Y轴为 指标值的 坐标系中的 线段， 该函数就是判断 两条线的 交叉情况 
    if (arr1.length !== arr2.length) {      // 首先要判断 比较的两个 数组 长度是否相等
        throw "array length not equal";     // 如果不相等 抛出错误，对于 不相等 的指标线  无法 判断相交
    }
    var n = 0;                              // 声明 变量 n  用来记录  交叉状态 ，初始  0 ，未相交 
    for (var i = arr1.length-1; i >= 0; i--) {      // 遍历 数组 arr1， 遍历顺序 为 从最后一个元素 向前 遍历
        if (typeof(arr1[i]) !== 'number' || typeof(arr2[i]) !== 'number') { // 当 arr1 或者 arr2 任何一个数组 为 非数值类型 （即 无效指标） 时，跳出 遍历循环。
            break;                                  // 跳出循环
        }
        if (arr1[i] < arr2[i]) {                    // 如果 arr1 小于 arr2 则 n-- ， 会记录 开始时 arr1、arr2 的相对 状态，（即 开始时  n 会根据 arr1[i] 、 arr2[i] 相对大小 自行调整，一旦出现 另一种 和 n 状态 相反的 arr1[i]、arr2[i] 大小关系， 即发生了 两条线交叉。）
            if (n > 0) {
                break;
            }
            n--;
        } else if (arr1[i] > arr2[i]) {             // 如果 arr1 大于 arr2 则 n++
            if (n < 0) {
                break;
            }
            n++;
        } else {                                    //  arr1[i] == arr2[i] ，则立即 跳出
            break;
        }
    }
    return n;                                       // 返回 n 值，代表 已经交叉了多少周期， 0 即 指标值相等。
};



///////////////////////////////////////////////////引入其他库
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
const Std = {
    _skip: function(a, b) {
        for (var c = 0, d = 0; c < a.length && (isNaN(a[c]) || d++,
        d != b); c++)
            ;
        return c
    },
    _sum: function(a, b) {
        for (var c = 0, d = 0; d < b; d++)
            isNaN(a[d]) || (c += a[d]);
        return c
    },
    _avg: function(a, b) {
        for (var c = 0, d = 0, e = 0; e < b; e++)
            isNaN(a[e]) || (d += a[e],
            c++);
        return d / c
    },
    _zeros: function(a) {
        for (var b = [], c = 0; c < a; c++)
            b.push(0);
        return b
    },
    _set: function(a, b, c, d) {
        for (var e = Math.min(a.length, c), f = b; f < e; f++)
            a[f] = d
    },
    _diff: function(a, b) {
        for (var c = [], d = 0; d < b.length; d++)
            isNaN(a[d]) || isNaN(b[d]) ? c.push(NaN) : c.push(a[d] - b[d]);
        return c
    },
    _move_diff: function(a) {
        for (var b = [], c = 1; c < a.length; c++)
            b.push(a[c] - a[c - 1]);
        return b
    },
    _sma: function(a, b) {
        var c = Std._zeros(a.length)
          , d = Std._skip(a, b);
        if (Std._set(c, 0, d, NaN),
        d < a.length)
            for (var e = 0, f = d; f < a.length; f++)
                f == d ? e = Std._sum(a, f + 1) : e += a[f] - a[f - b],
                c[f] = e / b;
        return c
    },
    _smma: function(a, b) {
        var c = Std._zeros(a.length)
          , d = Std._skip(a, b);
        if (Std._set(c, 0, d, NaN),
        d < a.length) {
            c[d] = Std._avg(a, d + 1);
            for (var e = d + 1; e < a.length; e++)
                c[e] = (c[e - 1] * (b - 1) + a[e]) / b
        }
        return c
    },
    _ema: function(a, b) {
        var c = Std._zeros(a.length)
          , d = 2 / (b + 1)
          , e = Std._skip(a, b);
        if (Std._set(c, 0, e, NaN),
        e < a.length) {
            c[e] = Std._avg(a, e + 1);
            for (var f = e + 1; f < a.length; f++)
                c[f] = (a[f] - c[f - 1]) * d + c[f - 1]
        }
        return c
    },
    _cmp: function(a, b, c, d) {
        for (var e = a[b], f = b; f < c; f++)
            e = d(a[f], e);
        return e
    },
    _filt: function(a, b, c, d, e) {
        if (a.length < 2)
            return NaN;
        for (var f = d, g = 0 !== b ? a.length - Math.min(a.length - 1, b) - 1 : 0, h = a.length - 2; h >= g; h--)
            f = void 0 !== c ? e(f, a[h][c]) : e(f, a[h]);
        return f
    },
    _ticks: function(a) {
        if (0 === a.length)
            return [];
        var b = [];
        if (void 0 !== a[0].Close)
            for (var c = 0; c < a.length; c++)
                b.push(a[c].Close);
        else
            b = a;
        return b
    }
};

const TA = {
    Highest: function(a, b, c) {
        return Std._filt(a, b, c, Number.MIN_VALUE, Math.max)
    },
    Lowest: function(a, b, c) {
        return Std._filt(a, b, c, Number.MAX_VALUE, Math.min)
    },
    MA: function(a, b) {
        return b = void 0 === b ? 9 : b,
        Std._sma(Std._ticks(a), b)
    },
    SMA: function(a, b) {
        return b = void 0 === b ? 9 : b,
        Std._sma(Std._ticks(a), b)
    },
    EMA: function(a, b) {
        return b = void 0 === b ? 9 : b,
        Std._ema(Std._ticks(a), b)
    },
    MACD: function(a, b, c, d) {
        b = void 0 === b ? 12 : b,
        c = void 0 === c ? 26 : c,
        d = void 0 === d ? 9 : d;
        var e = Std._ticks(a)
          , f = Std._ema(e, c)
          , g = Std._ema(e, b)
          , h = Std._diff(g, f)
          , i = Std._ema(h, d);
        return [h, i, Std._diff(h, i)]
    },
    BOLL: function(a, b, c) {
        b = void 0 === b ? 20 : b,
        c = void 0 === c ? 2 : c;
        for (var d = Std._ticks(a), e = b - 1; e < d.length && isNaN(d[e]); e++)
            ;
        var f = Std._zeros(d.length)
          , g = Std._zeros(d.length)
          , h = Std._zeros(d.length);
        Std._set(f, 0, e, NaN),
        Std._set(g, 0, e, NaN),
        Std._set(h, 0, e, NaN);
        for (var i = 0, j = e; j < d.length; j++) {
            if (j == e)
                for (var k = 0; k < b; k++)
                    i += d[k];
            else
                i = i + d[j] - d[j - b];
            for (var l = i / b, m = 0, k = j + 1 - b; k <= j; k++)
                m += (d[k] - l) * (d[k] - l);
            var n = Math.sqrt(m / b)
              , o = l + c * n
              , p = l - c * n;
            f[j] = o,
            g[j] = l,
            h[j] = p
        }
        return [f, g, h]
    },
    KDJ: function(a, b, c, d) {
        b = void 0 === b ? 9 : b,
        c = void 0 === c ? 3 : c,
        d = void 0 === d ? 3 : d;
        var e = Std._zeros(a.length);
        Std._set(e, 0, b - 1, NaN);
        for (var f = Std._zeros(a.length), g = Std._zeros(a.length), h = Std._zeros(a.length), i = Std._zeros(a.length), j = Std._zeros(a.length), k = 0; k < a.length; k++)
            i[k] = a[k].High,
            j[k] = a[k].Low;
        for (var k = 0; k < a.length; k++) {
            if (k >= b - 1) {
                var l = a[k].Close
                  , m = Std._cmp(i, k - (b - 1), k + 1, Math.max)
                  , n = Std._cmp(j, k - (b - 1), k + 1, Math.min);
                e[k] = m != n ? (l - n) / (m - n) * 100 : 100,
                f[k] = (1 * e[k] + (c - 1) * f[k - 1]) / c,
                g[k] = (1 * f[k] + (d - 1) * g[k - 1]) / d
            } else
                f[k] = g[k] = 50,
                e[k] = 0;
            h[k] = 3 * f[k] - 2 * g[k]
        }
        for (var k = 0; k < b - 1; k++)
            f[k] = g[k] = h[k] = NaN;
        return [f, g, h]
    },
    RSI: function(a, b) {
        b = void 0 === b ? 14 : b;
        var c, d = b, e = Std._zeros(a.length);
        if (Std._set(e, 0, e.length, NaN),
        a.length < d)
            return e;
        var f = Std._ticks(a)
          , g = Std._move_diff(f)
          , h = g.slice(0, d)
          , i = 0
          , j = 0;
        for (c = 0; c < h.length; c++)
            h[c] >= 0 ? i += h[c] : j += h[c];
        i /= d,
        j = -(j /= d);
        var k = 0 != j ? i / j : 0;
        e[d] = 100 - 100 / (1 + k);
        var l = 0
          , m = 0
          , n = 0;
        for (c = d + 1; c < f.length; c++)
            l = g[c - 1],
            l > 0 ? (m = l,
            n = 0) : (m = 0,
            n = -l),
            i = (i * (d - 1) + m) / d,
            j = (j * (d - 1) + n) / d,
            k = i / j,
            e[c] = 100 - 100 / (1 + k);
        return e
    },
    OBV: function(a) {
        if (0 === a.length)
            return [];
        if (void 0 === a[0].Close)
            throw "argument must KLine";
        for (var b = [], c = 0; c < a.length; c++)
            0 === c ? b[c] = a[c].Volume : a[c].Close >= a[c - 1].Close ? b[c] = b[c - 1] + a[c].Volume : b[c] = b[c - 1] - a[c].Volume;
        return b
    },
    ATR: function(a, b) {
        if (0 === a.length)
            return [];
        if (void 0 === a[0].Close)
            throw "argument must KLine";
        b = void 0 === b ? 14 : b;
        for (var c = Std._zeros(a.length), d = 0, e = 0, f = 0; f < a.length; f++) {
            var g = 0;
            g = 0 == f ? a[f].High - a[f].Low : Math.max(a[f].High - a[f].Low, Math.abs(a[f].High - a[f - 1].Close), Math.abs(a[f - 1].Close - a[f].Low)),
            d += g,
            e = f < b ? d / (f + 1) : ((b - 1) * e + g) / b,
            c[f] = e
        }
        return c
    },
    Alligator: function(a, b, c, d) {
        b = void 0 === b ? 13 : b,
        c = void 0 === c ? 8 : c,
        d = void 0 === d ? 5 : d;
        for (var e = [], f = 0; f < a.length; f++)
            e.push((a[f].High + a[f].Low) / 2);
        return [[NaN, NaN, NaN, NaN, NaN, NaN, NaN, NaN].concat(Std._smma(e, b)), [NaN, NaN, NaN, NaN, NaN].concat(Std._smma(e, c)), [NaN, NaN, NaN].concat(Std._smma(e, d))]
    },
    CMF: function(a, b) {
        b = b || 20;
        for (var c = [], d = 0, e = 0, f = [], g = [], h = 0; h < a.length; h++) {
            var i = a[h].High == a[h].Low ? 0 : (2 * a[h].Close - a[h].Low - a[h].High) / (a[h].High - a[h].Low) * a[h].Volume;
            f.push(i),
            g.push(a[h].Volume),
            d += i,
            e += a[h].Volume,
            h >= b && (d -= f.shift(),
            e -= g.shift()),
            c.push(d / e)
        }
        return c
    }
};

