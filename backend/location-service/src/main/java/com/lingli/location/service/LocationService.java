package com.lingli.location.service;

import com.lingli.location.dto.location.IpLocationDTO;
import com.lingli.location.dto.location.UserLocationDTO;
import com.lingli.location.vo.location.CityVO;
import com.lingli.location.vo.location.UserLocationVO;

import java.util.List;

/**
 * 地理位置服务接口
 *
 * @author lingli
 * @since 2023-11-28
 */
public interface LocationService {

    /**
     * 根据IP地址获取地理位置信息
     */
    IpLocationDTO getLocationByIp(String ipAddress);

    /**
     * 根据经纬度获取地理位置信息
     */
    IpLocationDTO getLocationByCoordinates(String longitude, String latitude);

    /**
     * 获取所有可用城市列表
     */
    List<CityVO> getAvailableCities();

    /**
     * 根据城市名称搜索城市
     */
    List<CityVO> searchCities(String keyword);

    /**
     * 获取用户的地理位置信息
     */
    UserLocationVO getUserLocation(Long userId);

    /**
     * 更新用户的地理位置信息
     */
    UserLocationVO updateUserLocation(Long userId, UserLocationDTO locationDTO);

    /**
     * 获取用户所在城市的公司信息
     */
    CityVO getCityCompanyInfo(Long userId);

    /**
     * 根据城市ID获取公司列表
     */
    List<CityVO> getCompaniesByCityId(Long cityId);

    /**
     * 检测用户是否需要设置地理位置
     */
    boolean needSetLocation(Long userId);

    /**
     * 设置默认地理位置（北京）
     */
    UserLocationVO setDefaultLocation(Long userId);

    /**
     * 获取城市列表（包含距离用户位置的距离）
     */
    List<CityVO> getNearbyCities(Long userId, Integer radius);
}