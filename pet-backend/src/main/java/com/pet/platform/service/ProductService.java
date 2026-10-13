package com.pet.platform.service;

import com.pet.platform.common.PageResult;
import com.pet.platform.dto.ProductSaveDTO;
import com.pet.platform.entity.Product;

public interface ProductService {

    /** 前台商品列表（仅上架） */
    PageResult<Product> pagePublic(Integer page, Integer size, String category);

    /** 商品详情（仅上架，前台） */
    Product getDetail(Long id);

    /** 管理员商品详情（含下架，后台） */
    Product getById(Long id);

    /** 管理员列表 */
    PageResult<Product> pageAdmin(Integer page, Integer size, String category, String keyword);

    /** 管理员列表（含状态筛选） */
    PageResult<Product> pageAdminWithStatus(Integer page, Integer size, String category, String keyword, Integer status);

    /** 新增 */
    Long save(ProductSaveDTO dto);

    /** 修改 */
    void update(Long id, ProductSaveDTO dto);

    /** 删除 */
    void delete(Long id);

    /** 上下架 */
    void updateStatus(Long id, Integer status);

    // ========== 商家端 ==========

    /** 商家商品列表（仅自身） */
    PageResult<Product> pageMerchant(Integer page, Integer size, String category, String keyword, Long merchantId);

    /** 商家商品详情（校验归属） */
    Product getMerchantById(Long id, Long merchantId);

    /** 商家新增商品 */
    Long saveAsMerchant(ProductSaveDTO dto, Long merchantId);

    /** 商家修改商品（校验归属） */
    void updateAsMerchant(Long id, ProductSaveDTO dto, Long merchantId);

    /** 商家删除商品（校验归属） */
    void deleteAsMerchant(Long id, Long merchantId);

    /** 商家上下架（校验归属） */
    void updateStatusAsMerchant(Long id, Integer status, Long merchantId);
}
