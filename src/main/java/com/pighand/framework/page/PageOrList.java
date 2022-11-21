package com.pighand.framework.page;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pighand.framework.PighandFrameworkConfig;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.springframework.util.Base64Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

/**
 * 分页查询，返回结果
 *
 * @param <T>
 * @author wangshuli
 */
@Data
@JsonIgnoreProperties({
    "pageType",
    "pageToken",
    "nextColumn",
    "nextValue",
    "total",
    "size",
    "current",
    "pages",
    "orders",
    "optimizeCountSql",
    "searchCount",
    "countId",
    "maxLimit"
})
public class PageOrList<T> extends Page<T> {

    /** page_token加密key */
    @Getter(AccessLevel.NONE)
    private String secretKey = PighandFrameworkConfig.page.getSecretKey();

    /** page_token加密算法 */
    @Getter(AccessLevel.NONE)
    private String secretAlgorithm = PighandFrameworkConfig.page.getSecretAlgorithm();

    /** 分页信息(接口返回) */
    private PageInfo page;

    /** 分页类型，默认page */
    private PageType pageType = PageType.PAGE;

    /** 分页token */
    private String pageToken;

    /** token模式，查询字段(有序、有索引字段) */
    private String nextColumn = PighandFrameworkConfig.page.getNextColumn();

    /** token模式，查询下页的值。上一页的最后一条数据 */
    private Object nextValue;

    public PageOrList() {
        super();
    }

    public PageOrList(PageType pageType) {
        super();

        this.pageType = pageType;
    }

    public PageOrList(long current, long size) {
        super(current, size);
    }

    public PageOrList(long current, long size, long total) {
        super(current, size, total);
    }

    public PageOrList(long current, long size, boolean searchCount) {
        super(current, size, searchCount);
    }

    public PageOrList(long current, long size, long total, boolean searchCount) {
        super(current, size, total, searchCount);
    }

    public PageOrList<T> setNextColumn(String nextColumn) {
        this.nextColumn = nextColumn;

        return this;
    }

    /**
     * 根据page_token，设置下页查询值
     *
     * @param pageToken
     */
    public void setPageToken(String pageToken) {
        if (StringUtils.isBlank(pageToken)) {
            throw new RuntimeException("page_token错误");
        }

        this.pageToken = pageToken;

        // 解密token值
        try {
            Cipher cipher = Cipher.getInstance(this.secretAlgorithm);
            SecretKeySpec sks = new SecretKeySpec(this.secretKey.getBytes(), this.secretAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, sks);
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(pageToken));
            this.nextValue = new String(bytes);
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 格式化返回结果
     *
     * <p>列表，不返回page对象
     *
     * <p>page模式，组合mybatis-plus返回的分页信息，聚合到pageInfo(接口返回，忽略其他mybatis-plus返回的数据)对象中
     *
     * <p>token模式，返回token
     *
     * @param records
     * @return
     */
    @Override
    public PageOrList<T> setRecords(List<T> records) {
        if (this.getSize() < 0) {
            this.pageType = PageType.LIST;
        }

        if (PageType.LIST.equals(this.pageType)) {
            // list
            this.page = null;
        } else if (PageType.PAGE.equals(this.pageType)) {
            // page
            this.page =
                    new PageInfo(
                            this.pageType,
                            super.getTotal(),
                            super.getSize(),
                            super.getCurrent(),
                            super.getPages(),
                            null);
        } else if (PageType.NEXT_TOKEN.equals(this.pageType)) {
            // token
            this.pageToken = null;

            // 查询前size+1，用来判断是否有下页数据，返回时-1
            super.setSize(super.getSize() - 1);

            // 返回数据的条数 > siz，有下页数据，处理token
            if (records.size() > super.getSize()) {
                // 最后一条数据
                T lastObject = records.get(records.size() - 1);

                // 根据最后一条数据，生成token
                try {
                    PropertyDescriptor proDescriptor =
                            new PropertyDescriptor(this.nextColumn, lastObject.getClass());
                    Method methodGet = proDescriptor.getReadMethod();
                    Object nextValue = methodGet.invoke(lastObject);

                    Cipher cipher = Cipher.getInstance(this.secretAlgorithm);
                    SecretKeySpec sks =
                            new SecretKeySpec(this.secretKey.getBytes(), this.secretAlgorithm);
                    cipher.init(Cipher.ENCRYPT_MODE, sks);
                    byte[] bytes = cipher.doFinal(String.valueOf(nextValue).getBytes());

                    this.pageToken = Base64Utils.encodeToString(bytes);
                } catch (IllegalAccessException
                        | NoSuchAlgorithmException
                        | NoSuchPaddingException
                        | InvalidKeyException
                        | IllegalBlockSizeException
                        | BadPaddingException
                        | InvocationTargetException
                        | IntrospectionException e) {
                    throw new RuntimeException(e);
                }

                // 删除多返回的一条记录
                records.remove(records.size() - 1);
            }

            this.page = new PageInfo(this.pageType, null, null, null, null, this.pageToken);
        }

        this.records = records;

        return this;
    }

    /**
     * 转list
     *
     * @return
     */
    public List<T> toList() {
        return this.records;
    }
}
