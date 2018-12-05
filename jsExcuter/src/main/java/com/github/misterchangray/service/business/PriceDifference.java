package com.github.misterchangray.service.business;

import com.github.misterchangray.common.enums.CoinOtc;
import com.github.misterchangray.common.enums.QuartzEnum;
import com.github.misterchangray.common.utils.DateUtils;
import com.github.misterchangray.controller.common.vo.MonitorConfig;
import com.github.misterchangray.controller.common.vo.MonitorUserInfo;
import com.github.misterchangray.controller.common.vo.PhoneCallRecord;
import com.github.misterchangray.service.BaseService;
import com.github.misterchangray.service.common.MongoDbService;
import com.github.misterchangray.service.platform.OKEXService;
import com.github.misterchangray.service.platform.vo.Kline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 扫描价差；当价差大于指定值时打电话通知
 */
@Service
public class PriceDifference extends BaseService {
    private static final Logger log = LoggerFactory.getLogger(PriceDifference.class);
    private String message = "当前季度eos合约最近15分钟价差大于百分之二";

    @Autowired
    private PhoneService phoneService;

    @Autowired
    private OKEXService okexService;

    @Autowired
    private MongoDbService mongoDbService;

    @Value("${server.monitor.enabled:false}")
    private String monitorEnabled;


    /**
     * 第一期监控币种：
     * BTC EOS 以OKEX季度期货行情为准
     * 以分钟计，取当前分钟~之前20分钟（页面可分币种配置）的数据
     * A. 价格预警
     * 20分钟（页面配置为准）的 （最高价-最低价）/当前价>0.02 时，电话通知相关人（页面可配置 姓名、电话 不区分币种）  0.02（页面可分币种配置）
     * B. 交易量预警
     * 20分钟（页面配置为准）的 最大交易量/分钟平均交易量>3 且 分钟最大交易量/分钟第二大交易量>2 时，电话通知相关人  3 2（页面可分币种配置）
     *
     * 同一个号码在 一个币种两个时间周期内40分钟（页面配置的20分钟 X 2）只打一次电话
     */
    public void scanner() {
        if (!monitorEnabled.equals("true")) {
            return;
        }
        List<MonitorConfig> monitorConfigList = mongoDbService.list(MonitorConfig.collectionName, MonitorConfig.class);
        if (monitorConfigList == null || monitorConfigList.size() == 0) {
            log.error("行情监控未配置, 请先进行配置.");
            return;
        }
        monitorConfigList.forEach(this::scannerByMonitorConfig);

    }

    private void scannerByMonitorConfig(MonitorConfig config) {
//        List<Kline> res = okexService.getRecordsOtc(CoinOtc.eos, "quarter", "1min", 20l);
        log.info("[{}]配置读取: {}", CoinOtc.eos, config);
        CoinOtc coinOtc = config.getCoinOtc();
        String contractType = config.getContractType();
        String cycle = config.getCycle();
        Long size = config.getSize();
        Double priceSurgeThreshold = config.getPriceSurgeThreshold();
        Double maxVolumeRateThreshold = config.getMaxVolumeRateThreshold();
        Double maxSecondVolumeVaryThreshold = config.getMaxSecondVolumeVaryThreshold();
        List<Kline> res = okexService.getRecordsOtc(coinOtc, contractType, cycle, size);
        if (res != null) {
            log.info("返回{}组数据[{}]", res.size(), res);
        }
        if(null != res && 10 < res.size()) {
            // A. 价格预警
            double maxPrice = 0;
            double minPrice = Integer.MAX_VALUE;
            for(Kline tmp : res) {
                // 取出最高价格
                if(tmp.getHigh() > maxPrice) maxPrice = tmp.getHigh();
                // 取出最低价格
                if(tmp.getLow() < minPrice) minPrice = tmp.getLow();
            }
            log.info("最高价格[{}]", maxPrice);
            log.info("最低价格[{}]", minPrice);
            // （最高价-最低价）/当前价>0.02 时，电话通知相关人员
            double priceSurge = (maxPrice - minPrice) / res.get(res.size() - 1).getClose();
            log.info("价格预警[(最高价-最低价)/当前价] - 计算出的值: {}; 设定的阀值: {}", priceSurge, priceSurgeThreshold);
            if(priceSurge > priceSurgeThreshold) {
                callPhone(config, "价格预警，计算出的值为" + String.format("%.2f", priceSurge) + "，超过", priceSurgeThreshold);
            }
            // B. 交易量预警
            List<Double> volumes = res.stream().map(Kline::getVol).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            log.info("交易量[{}]", volumes);
            Double averageVolume = volumes.stream().collect(Collectors.averagingDouble(Double::doubleValue));
            log.info("交易量平均值[{}]", averageVolume);
            Double maxVolume = volumes.get(0);
            log.info("交易量最大值[{}]", maxVolume);
            Double maxSecondVolume = volumes.get(1);
            log.info("交易量第二大值[{}]", maxSecondVolume);
            // 最大交易量/分钟平均交易量 > 3
            Double maxVolumeRate = maxVolume / averageVolume;
            log.info("交易量预警[A][最大交易量/分钟平均交易量] - 计算出的值: {}; 设定的阀值: {}", maxVolumeRate, maxVolumeRateThreshold);
            if (maxVolumeRate > maxVolumeRateThreshold) {
                callPhone(config, "交易量预警A，计算出的值为" + String.format("%.2f", maxVolumeRate) + "，超过", maxVolumeRateThreshold);
            }
            // 分钟最大交易量/分钟第二大交易量>2
            Double maxSecondVolumeVary = maxVolume / maxSecondVolume;
            log.info("交易量预警[B][分钟最大交易量/分钟第二大交易量] - 计算出的值: {}; 设定的阀值: {}", maxSecondVolumeVary, maxSecondVolumeVaryThreshold);
            if (maxSecondVolumeVary > maxSecondVolumeVaryThreshold) {
                callPhone(config, "交易量预警B，计算出的值为" + String.format("%.2f", maxSecondVolumeVary) + "，超过", maxSecondVolumeVaryThreshold);
            }
        }
    }

    private void callPhone(MonitorConfig config, String callMsg, Double callVal) {
        List<MonitorUserInfo> users = mongoDbService.list(MonitorUserInfo.collectionName, MonitorUserInfo.class);
        if (users == null || users.size() == 0) {
            log.info("监控通知用户未配置, 请先进行配置.");
            return;
        }

        // 为配置的用户拨打电话
        users.forEach(user -> {
            String phone = user.getPhone();
            String username = user.getUsername();
            String coinName = config.getCoinOtc().toString();

            Query query = Query
                    .query(Criteria.where("phone").is(phone).and("coinOtc").is(config.getCoinOtc()))
                    .with(new Sort(Sort.Direction.DESC, "createTime"))
                    .limit(1);
            List<PhoneCallRecord> list = mongoDbService.list(query, PhoneCallRecord.collectionName, PhoneCallRecord.class);
            if (list != null && list.size() == 1) {
                // 同一个号码在 一个币种两个时间周期内40分钟（页面配置的20分钟 X 2）只打一次电话
                PhoneCallRecord callRecord = list.get(0);
                long timeThreshold = DateUtils.getTimeMillis(config.getCycle()) * config.getSize() * 2;
                if (System.currentTimeMillis() - callRecord.getCreateTime() < timeThreshold) {
                    log.info("上次拨打时间{} , 未超过时间{}阀值, 无需向[{}]拨打电话", callRecord.getCreateTime(), timeThreshold, phone);
                    return;
                }
            }

            log.info("低于监控阀值, 向[{}]拨打电话", phone);
            PhoneCallRecord record = new PhoneCallRecord();
            record.setPhone(phone);
            record.setCoinOtc(config.getCoinOtc());
            record.setCreateTime(System.currentTimeMillis());
            mongoDbService.insert(record, PhoneCallRecord.collectionName);

            phoneService.call(phone, username, coinName, callMsg, callVal + "");
        });

    }

    @Override
    public Boolean quartz(QuartzEnum quartzEnum, long execTime) {
        if(QuartzEnum.Minute  == quartzEnum) {
            scanner();
        }
        return true;
    }
}
