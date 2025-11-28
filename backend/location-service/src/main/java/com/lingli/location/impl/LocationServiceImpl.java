package com.lingli.location.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingli.common.exception.BusinessException;
import com.lingli.location.dto.location.IpLocationDTO;
import com.lingli.location.dto.location.UserLocationDTO;
import com.lingli.location.entity.UserLocation;
import com.lingli.location.mapper.UserLocationMapper;
import com.lingli.location.service.LocationService;
import com.lingli.location.vo.location.CityVO;
import com.lingli.location.vo.location.UserLocationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 地理位置服务实现
 *
 * @author lingli
 * @since 2023-11-28
 */
@Slf4j
@Service
@Transactional
public class LocationServiceImpl extends ServiceImpl<UserLocationMapper, UserLocation> implements LocationService {

    @Autowired
    private UserLocationMapper userLocationMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 缓存键前缀
    private static final String CACHE_KEY_IP_LOCATION = "location:ip:";
    private static final String CACHE_KEY_CITIES = "location:cities";
    private static final String CACHE_KEY_CITY_COMPANIES = "location:city:companies:";

    @Override
    public IpLocationDTO getLocationByIp(String ipAddress) {
        // 检查缓存
        String cacheKey = CACHE_KEY_IP_LOCATION + ipAddress;
        IpLocationDTO cached = (IpLocationDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // TODO: 集成真实的IP地理位置服务
        // 这里使用模拟数据，实际项目中应该调用如百度地图、腾讯地图等第三方服务
        IpLocationDTO result = simulateLocationByIp(ipAddress);
        
        // 缓存结果
        if (result != null) {
            redisTemplate.opsForValue().set(cacheKey, result, 1, TimeUnit.HOURS);
        }
        
        return result;
    }

    @Override
    public IpLocationDTO getLocationByCoordinates(String longitude, String latitude) {
        // TODO: 实现根据经纬度获取地理位置
        // 这里使用模拟数据
        return simulateLocationByCoordinates(longitude, latitude);
    }

    @Override
    public List<CityVO> getAvailableCities() {
        // 检查缓存
        List<CityVO> cached = (List<CityVO>) redisTemplate.opsForValue().get(CACHE_KEY_CITIES);
        if (cached != null) {
            return cached;
        }

        // 从数据库获取城市信息
        List<CityVO> cities = getCitiesFromDatabase();
        
        // 缓存结果
        redisTemplate.opsForValue().set(CACHE_KEY_CITIES, cities, 1, TimeUnit.HOURS);
        
        return cities;
    }

    @Override
    public List<CityVO> searchCities(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAvailableCities();
        }

        List<CityVO> allCities = getAvailableCities();
        List<CityVO> result = new ArrayList<>();
        
        String lowerKeyword = keyword.toLowerCase();
        for (CityVO city : allCities) {
            if (city.getCityName().toLowerCase().contains(lowerKeyword) ||
                city.getProvince().toLowerCase().contains(lowerKeyword)) {
                result.add(city);
            }
        }
        
        return result;
    }

    @Override
    public UserLocationVO getUserLocation(Long userId) {
        UserLocation userLocation = userLocationMapper.selectOne(
            new LambdaQueryWrapper<UserLocation>()
                .eq(UserLocation::getUserId, userId)
                .eq(UserLocation::getIsDeleted, 0)
        );

        UserLocationVO userLocationVO = new UserLocationVO();
        if (userLocation != null) {
            BeanUtil.copyProperties(userLocation, userLocationVO);
            userLocationVO.setHasLocation(true);
            userLocationVO.setLocationSourceDesc(getLocationSourceDesc(userLocation.getLocationSource()));
            
            // 获取城市信息
            CityVO cityInfo = getCityInfo(userLocation.getCity());
            userLocationVO.setCityInfo(cityInfo);
        } else {
            userLocationVO.setHasLocation(false);
        }
        
        return userLocationVO;
    }

    @Override
    public UserLocationVO updateUserLocation(Long userId, UserLocationDTO locationDTO) {
        // 检查是否已存在用户位置记录
        UserLocation existingLocation = userLocationMapper.selectOne(
            new LambdaQueryWrapper<UserLocation>()
                .eq(UserLocation::getUserId, userId)
                .eq(UserLocation::getIsDeleted, 0)
        );

        UserLocation userLocation = BeanUtil.copyProperties(locationDTO, UserLocation.class);
        userLocation.setUserId(userId);

        if (existingLocation != null) {
            // 更新现有记录
            userLocation.setId(existingLocation.getId());
            userLocationMapper.updateById(userLocation);
        } else {
            // 创建新记录
            userLocationMapper.insert(userLocation);
        }

        log.info("更新用户地理位置: userId={}, city={}", userId, locationDTO.getCity());
        
        // 返回最新的地理位置信息
        return getUserLocation(userId);
    }

    @Override
    public CityVO getCityCompanyInfo(Long userId) {
        UserLocationVO userLocation = getUserLocation(userId);
        if (!userLocation.getHasLocation()) {
            throw new BusinessException(30001, "用户未设置地理位置");
        }
        
        return getCityInfo(userLocation.getCity());
    }

    @Override
    public List<CityVO> getCompaniesByCityId(Long cityId) {
        String cacheKey = CACHE_KEY_CITY_COMPANIES + cityId;
        List<CityVO> cached = (List<CityVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<CityVO> companies = getCompaniesFromDatabase(cityId);
        redisTemplate.opsForValue().set(cacheKey, companies, 30, TimeUnit.MINUTES);
        
        return companies;
    }

    @Override
    public boolean needSetLocation(Long userId) {
        UserLocationVO userLocation = getUserLocation(userId);
        return !userLocation.getHasLocation();
    }

    @Override
    public UserLocationVO setDefaultLocation(Long userId) {
        UserLocationDTO defaultLocation = new UserLocationDTO();
        defaultLocation.setProvince("北京市");
        defaultLocation.setCity("北京市");
        defaultLocation.setDistrict("朝阳区");
        defaultLocation.setLocationSource("user_select");
        
        return updateUserLocation(userId, defaultLocation);
    }

    @Override
    public List<CityVO> getNearbyCities(Long userId, Integer radius) {
        UserLocationVO userLocation = getUserLocation(userId);
        if (!userLocation.getHasLocation()) {
            throw new BusinessException(30001, "用户未设置地理位置");
        }

        // TODO: 实现基于距离的城市查询
        // 这里返回所有可用城市，实际项目中应该按距离排序
        List<CityVO> allCities = getAvailableCities();
        
        // 为每个城市计算距离（模拟）
        for (CityVO city : allCities) {
            // 模拟计算距离（实际项目中应该使用地理信息系统计算）
            int distance = (int) (Math.random() * radius * 1000);
            city.setDistance(distance);
            city.setIsUserCity(city.getCityName().equals(userLocation.getCity()));
        }
        
        // 按距离排序
        allCities.sort((c1, c2) -> Integer.compare(c1.getDistance(), c2.getDistance()));
        
        return allCities;
    }

    // ========== 私有方法 ==========

    /**
     * 模拟IP地理位置解析
     */
    private IpLocationDTO simulateLocationByIp(String ipAddress) {
        IpLocationDTO result = new IpLocationDTO();
        result.setIp(ipAddress);
        result.setCountry("中国");
        
        // 根据IP段模拟不同城市
        if (ipAddress.startsWith("192.168")) {
            result.setProvince("北京市");
            result.setCity("北京市");
            result.setDistrict("朝阳区");
        } else if (ipAddress.startsWith("10.0")) {
            result.setProvince("上海市");
            result.setCity("上海市");
            result.setDistrict("浦东新区");
        } else {
            result.setProvince("广东省");
            result.setCity("深圳市");
            result.setDistrict("南山区");
        }
        
        result.setLongitude("116.407526");
        result.setLatitude("39.90403");
        result.setIsp("未知");
        result.setIsChina(true);
        
        return result;
    }

    /**
     * 模拟经纬度地理位置解析
     */
    private IpLocationDTO simulateLocationByCoordinates(String longitude, String latitude) {
        // TODO: 实现真实的经纬度地址解析
        return simulateLocationByIp("0.0.0.0");
    }

    /**
     * 从数据库获取城市信息
     */
    private List<CityVO> getCitiesFromDatabase() {
        List<CityVO> cities = new ArrayList<>();
        
        // 模拟城市数据，实际项目中应该从数据库查询
        String[] cityNames = {"北京市", "上海市", "广州市", "深圳市", "杭州市", "南京市", "成都市", "武汉市", "西安市", "重庆市"};
        
        for (int i = 0; i < cityNames.length; i++) {
            CityVO city = new CityVO();
            city.setCityId((long) (i + 1));
            city.setCityName(cityNames[i]);
            city.setProvince(getProvinceByCity(cityNames[i]));
            city.setCityCode("CITY" + (i + 1));
            city.setEnabled(true);
            city.setCompanies(getMockCompanies());
            cities.add(city);
        }
        
        return cities;
    }

    /**
     * 从数据库获取公司信息
     */
    private List<CityVO> getCompaniesFromDatabase(Long cityId) {
        List<CityVO> companies = new ArrayList<>();
        
        // 模拟公司数据
        for (int i = 0; i < 3; i++) {
            CityVO.CompanyVO company = new CityVO.CompanyVO();
            company.setCompanyId(cityId * 100 + i + 1);
            company.setCompanyName("灵力检测" + (char)('A' + i) + "公司");
            company.setCompanyCode("COMPANY" + (cityId * 100 + i + 1));
            company.setContactPhone("400-" + (1000000 + i));
            company.setContactEmail("company" + i + "@lingli.com");
            company.setAddress("地址信息" + i);
            company.setDefault(i == 0);
            
            CityVO cityVO = new CityVO();
            cityVO.setCompanies(List.of(company));
            companies.add(cityVO);
        }
        
        return companies;
    }

    /**
     * 根据城市获取省份
     */
    private String getProvinceByCity(String cityName) {
        if (cityName.contains("北京") || cityName.contains("上海")) {
            return cityName;
        }
        if (cityName.contains("广州") || cityName.contains("深圳")) {
            return "广东省";
        }
        if (cityName.contains("杭州")) {
            return "浙江省";
        }
        if (cityName.contains("南京")) {
            return "江苏省";
        }
        if (cityName.contains("成都")) {
            return "四川省";
        }
        if (cityName.contains("武汉")) {
            return "湖北省";
        }
        if (cityName.contains("西安")) {
            return "陕西省";
        }
        if (cityName.contains("重庆")) {
            return "重庆市";
        }
        return "其他";
    }

    /**
     * 获取城市信息
     */
    private CityVO getCityInfo(String cityName) {
        List<CityVO> allCities = getAvailableCities();
        return allCities.stream()
            .filter(city -> cityName.equals(city.getCityName()))
            .findFirst()
            .orElse(null);
    }

    /**
     * 获取位置来源描述
     */
    private String getLocationSourceDesc(String source) {
        switch (source) {
            case "ip": return "IP地址定位";
            case "user_select": return "用户选择";
            case "gps": return "GPS定位";
            default: return "未知";
        }
    }

    /**
     * 模拟公司数据
     */
    private List<CityVO.CompanyVO> getMockCompanies() {
        List<CityVO.CompanyVO> companies = new ArrayList<>();
        
        for (int i = 0; i < 2; i++) {
            CityVO.CompanyVO company = new CityVO.CompanyVO();
            company.setCompanyId((long) (i + 1));
            company.setCompanyName("灵力检测公司" + (char)('A' + i));
            company.setCompanyCode("COMPANY" + (i + 1));
            company.setContactPhone("400-" + (1000000 + i));
            company.setContactEmail("company" + i + "@lingli.com");
            company.setAddress("默认地址" + i);
            company.setDefault(i == 0);
            companies.add(company);
        }
        
        return companies;
    }
}