//全局配置
/**
 * 此处配置了两个交易所
 */
Configs([
    {
        __AKEY: "597febd1-7a45-*-*-986d7f1457c1",
        __SKEY: "1558120C1938C**40526D67",
        __ORG: "OKEX",
        __COIN: "btc",
        __TYPE: "otc",
        __ContractType: "quarter",
        __Direction: "sell",
        __MarginLevel: "10",
        __DefaultPeriod: PERIOD_M5
    },
    {
        __AKEY: "kPEL3JCX9**oCFsq9d", //api key
        __SKEY: "DwAMj7Bql9xoSIYmH**6jx7REv8", //secrt key
        __ORG: "Bitmex", //交易平台,多选1;[Bitmex, OKEX]
        __COIN: "btc", //交易币种,
        __TYPE: "otc", //期货还是现货;多选一;[spot, otc]
        __ContractType: "XBTUSD", // 合约类型
        __Direction: "buy", //默认下单方向
        __MarginLevel: "10", //默认杠杆大小
        __DefaultPeriod: PERIOD_H1 //默认K线图周期
    }

]);


var org = 0;
function main(v) {
    var res = exchanges[org].GetAccount();
    Log("账户信息:", JSON.stringify(res))

    Log("格式化一个小数", _N(3, 3.1415926))
    Log("显示当前时间戳", _D());
    exchanges[org].SetPrecision(3, 8);

    exchanges[org].SetMarginLevel("10");
    exchanges[org].SetDirection("buy");
    var id = exchanges[org].Buy(5000.12345, 1);
    Log("orderid1:", id);
    Sleep(5000);
    var h = _C(exchanges[org].GetOrders);
    Log("下单成功,当前未完成订单情况", h)
    if (0 != h.length) {
        var h = exchanges[org].CancelOrder(id);
        Log("取消订单,下单未成交,撤销订单", h)
    }


    exchanges[org].SetDirection("sell");
    id = exchanges[org].Sell(-1, 1);
    Log("orderid2:", id)
    var h = _C(exchanges[org].GetPosition);
    Log("下市价单空单成功,仓位情况", JSON.stringify(h))


    var GetDepth = _C(exchanges[org].GetDepth);
    exchanges[org].SetDirection("buy");
    id = exchanges[org].Buy(GetDepth.Asks[0].Price, 1);
    Log("orderid3:", id)
    var h = _C(exchanges[org].GetPosition);
    Log("下限价多单成功，仓位情况", JSON.stringify(h))

    var res = exchanges[org].GetAccount();
    Log("账户信息2:", JSON.stringify(res))

    var index = 0;
    while (true) {
        index++;
        var GetRecords = _C(exchanges[org].GetRecords, PERIOD_H1);
        var GetTicker = _C(exchanges[org].GetTicker);
        var ta15 = TA.MA(GetRecords, 15); //10天均线

        if (index > 5) {
            exchanges[org].SetDirection("closesell");
            id = exchanges[org].Buy(-1, 1);
            break;
        }
        Log("GetRecords", JSON.stringify(GetRecords));
        Log("GetTicker", JSON.stringify(GetTicker));
        Log("GetDepth", JSON.stringify(GetDepth));
        Log("ta15", JSON.stringify(ta15));
        Sleep(15000);
    }

}


function onexit() {
    exchanges[org].SetDirection("closebuy");
    id = exchanges[org].Sell(-1, 1);
    Log("程序执行完毕,")
}

function init() {
    Log("程序开始执行")
}

