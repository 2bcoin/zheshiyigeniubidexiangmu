package com.github.misterchangray.libs.okcoin.rest.future;

import java.io.IOException;

import org.apache.http.HttpException;


/**
 * 期货行情，交易 REST API
 * @author zhangchi
 *
 */
public interface IFutureRestApi {
	

     /**
      * 期货行情
      * @param symbol   btc_usd:比特币    ltc_usd :莱特币
      * @param contractType  合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
      * @return
      * @throws HttpException
      * @throws IOException
     */
     public String future_ticker(String symbol, String contractType) throws HttpException, IOException;
	
     /**
      * 期货指数
      * @param symbol   btc_usd:比特币    ltc_usd :莱特币
      * @return
      * @throws HttpException
      * @throws IOException
     */
     public String future_index(String symbol) throws HttpException, IOException;
	
     /**
      * 期货交易记录
      * @param symbol    btc_usd:比特币    ltc_usd :莱特币
      * @param contractType   合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
      * @return
      * @throws HttpException
      * @throws IOException
     */
     public String future_trades(String symbol, String contractType) throws HttpException, IOException;
	
     /**
      * 期货深度
      * @param symbol  btc_usd:比特币    ltc_usd :莱特币
      * @param contractType  合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
      * @return
      * @throws HttpException
      * @throws IOException
     */
      public String future_depth(String symbol, String contractType) throws HttpException, IOException;
	
      /**
       * 汇率查询
       * @return
       * @throws IOException 
       * @throws HttpException 
      */
      public String exchange_rate() throws HttpException, IOException;
	
      /**
       * 取消订单
       * @param symbol   btc_usd:比特币    ltc_usd :莱特币
       * @param contractType    合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
       * @param orderId   订单ID
       * @return
       * @throws HttpException
       * @throws IOException
      */
      public String future_cancel(String symbol, String contractType, String orderId) throws HttpException, IOException;
	
      /**
       * 期货下单
       * @param symbol   btc_usd:比特币    ltc_usd :莱特币
       * @param contractType   合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
       * @param price  价格
       * @param amount  委托数量
       * @param type   1:开多   2:开空   3:平多   4:平空
       * @param matchPrice  是否为对手价 0:不是    1:是   ,当取值为1时,price无效
       * @return
       * @throws HttpException
       * @throws IOException
      */
      public String future_trade(String symbol, String contractType, String price, String amount, String type, String matchPrice) throws HttpException, IOException;
	

      /**
       * 期货账户信息
       * @return
       * @throws IOException 
       * @throws HttpException 
      */
       public String future_userinfo() throws HttpException, IOException;
	
       /**
        * 期货逐仓账户信息
        * @return
        * @throws HttpException
        * @throws IOException
       */
	public String future_userinfo_4fix()throws HttpException, IOException;
	
        /**
         * 用户持仓查询
         * @param symbol   btc_usd:比特币    ltc_usd :莱特币
         * @param contractType   合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
         * @return
         * @throws HttpException
         * @throws IOException
        */
	public String future_position(String symbol, String contractType) throws HttpException, IOException;
	
	/**
	 * 用户逐仓持仓查询
	 * @param symbol   btc_usd:比特币    ltc_usd :莱特币
	 * @param contractType   合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
	 * @return
	 * @throws HttpException
	 * @throws IOException
	*/
	public String future_position_4fix(String symbol, String contractType) throws HttpException, IOException;
	
        /**
         * 获取用户订单信息
         * @param symbol   btc_usd:比特币    ltc_usd :莱特币
         * @param contractType   合约类型: this_week:当周   next_week:下周   month:当月   quarter:季度
         * @param orderId   订单ID(-1查询全部未成交订单，否则查询相应单号的订单)
         * @param status   查询状态：1:未完成(最近七天的数据)  2:已完成(最近七天的数据)
         * @param currentPage 当前页数
         * @param pageLength  每页获取条数，最多不超过50
         * @return
         * @throws HttpException
         * @throws IOException
        */
	public String future_order_info(String symbol, String contractType, String orderId, String status, String currentPage, String pageLength) throws HttpException, IOException;
    
    
}
