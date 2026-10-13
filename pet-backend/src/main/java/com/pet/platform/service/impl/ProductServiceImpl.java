package com.pet.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.platform.common.BusinessException;
import com.pet.platform.common.ErrorCode;
import com.pet.platform.common.PageResult;
import com.pet.platform.dto.ProductSaveDTO;
import com.pet.platform.entity.Product;
import com.pet.platform.mapper.ProductMapper;
import com.pet.platform.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 商品服务实现
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public PageResult<Product> pagePublic(Integer page, Integer size, String category) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
               .eq(StringUtils.hasText(category), Product::getCategory, category)
               .orderByDesc(Product::getCreateTime);
        return doPage(page, size, wrapper);
    }

    @Override
    public Product getDetail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "商品不存在或已下架");
        }
        return product;
    }

    @Override
    public Product getById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "商品不存在");
        }
        return product;
    }

    @Override
    public PageResult<Product> pageAdmin(Integer page, Integer size, String category, String keyword) {
        return pageAdminWithStatus(page, size, category, keyword, null);
    }

    @Override
    public PageResult<Product> pageAdminWithStatus(Integer page, Integer size, String category, String keyword, Integer status) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(category), Product::getCategory, category)
               .like(StringUtils.hasText(keyword), Product::getName, keyword)
               .eq(status != null, Product::getStatus, status)
               .orderByDesc(Product::getCreateTime);
        return doPage(page, size, wrapper);
    }

    @Override
    public Long save(ProductSaveDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock() == null ? 0 : dto.getStock());
        product.setCoverImage(dto.getCoverImage());
        product.setImages(dto.getImages());
        product.setCategory(dto.getCategory());
        product.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.insert(product);
        log.info("新增商品 id={} name={}", product.getId(), dto.getName());
        return product.getId();
    }

    @Override
    public void update(Long id, ProductSaveDTO dto) {
        Product existing = getAdminById(id);
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        if (dto.getStock() != null) {
            existing.setStock(dto.getStock());
        }
        existing.setCoverImage(dto.getCoverImage());
        existing.setImages(dto.getImages());
        existing.setCategory(dto.getCategory());
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(existing);
        log.info("修改商品 id={}", id);
    }

    @Override
    public void delete(Long id) {
        getAdminById(id);
        productMapper.deleteById(id);
        log.info("删除商品 id={}", id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Product existing = getAdminById(id);
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(existing);
        log.info("更新商品状态 id={} status={}", id, status);
    }

    private Product getAdminById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "商品不存在");
        }
        return product;
    }

    private PageResult<Product> doPage(Integer page, Integer size, LambdaQueryWrapper<Product> wrapper) {
        int p = page == null || page < 1 ? 1 : page;
        int s = size == null || size < 1 ? 10 : Math.min(size, 50);
        IPage<Product> pageObj = new Page<>(p, s);
        IPage<Product> result = productMapper.selectPage(pageObj, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    // ========== 商家端 ==========

    @Override
    public PageResult<Product> pageMerchant(Integer page, Integer size, String category, String keyword, Long merchantId) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getMerchantId, merchantId)
               .eq(StringUtils.hasText(category), Product::getCategory, category)
               .like(StringUtils.hasText(keyword), Product::getName, keyword)
               .orderByDesc(Product::getCreateTime);
        return doPage(page, size, wrapper);
    }

    @Override
    public Product getMerchantById(Long id, Long merchantId) {
        Product product = getAdminById(id);
        checkOwnership(product, merchantId);
        return product;
    }

    @Override
    public Long saveAsMerchant(ProductSaveDTO dto, Long merchantId) {
        Product product = new Product();
        product.setMerchantId(merchantId);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock() == null ? 0 : dto.getStock());
        product.setCoverImage(dto.getCoverImage());
        product.setImages(dto.getImages());
        product.setCategory(dto.getCategory());
        product.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.insert(product);
        log.info("商家新增商品 merchantId={} id={} name={}", merchantId, product.getId(), dto.getName());
        return product.getId();
    }

    @Override
    public void updateAsMerchant(Long id, ProductSaveDTO dto, Long merchantId) {
        Product existing = getMerchantById(id, merchantId);
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        if (dto.getStock() != null) {
            existing.setStock(dto.getStock());
        }
        existing.setCoverImage(dto.getCoverImage());
        existing.setImages(dto.getImages());
        existing.setCategory(dto.getCategory());
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(existing);
        log.info("商家修改商品 merchantId={} id={}", merchantId, id);
    }

    @Override
    public void deleteAsMerchant(Long id, Long merchantId) {
        getMerchantById(id, merchantId);
        productMapper.deleteById(id);
        log.info("商家删除商品 merchantId={} id={}", merchantId, id);
    }

    @Override
    public void updateStatusAsMerchant(Long id, Integer status, Long merchantId) {
        Product existing = getMerchantById(id, merchantId);
        existing.setStatus(status);
        existing.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(existing);
        log.info("商家更新商品状态 merchantId={} id={} status={}", merchantId, id, status);
    }

    private void checkOwnership(Product product, Long merchantId) {
        if (product.getMerchantId() == null || !product.getMerchantId().equals(merchantId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION.getCode(), "无权操作该商品");
        }
    }
}
