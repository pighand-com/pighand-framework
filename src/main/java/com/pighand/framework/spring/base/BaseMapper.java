package com.pighand.framework.spring.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.pighand.framework.spring.PighandFrameworkConfig;
import com.pighand.framework.spring.page.NextToken;
import com.pighand.framework.spring.page.PageInfo;
import com.pighand.framework.spring.page.PageOrList;
import com.pighand.framework.spring.page.PageType;
import com.pighand.framework.spring.util.VerifyUtils;

import java.util.List;

/**
 * mapper 基础父类
 *
 * @author wangshuli
 */
public interface BaseMapper<T extends BaseDomain> extends com.mybatisflex.core.BaseMapper<T> {
    /**
     * 分页查询
     *
     * @param pageInfo
     * @return
     * @see #page(PageInfo, QueryWrapper)
     */
    default PageOrList<T> page(PageInfo pageInfo) {
        return this.page(pageInfo, null);
    }

    /**
     * 分页查询
     * <p>1. 如果没有设置pageType或AUTO，根据pageSize、nextToken判断pageType
     * <p>2. pageType判断分页类型，并初始化分页数据
     * <p>3. 根据分页数据查询数据
     *
     * @param pageInfo
     * @param queryWrapper
     * @return
     */
    default PageOrList<T> page(PageInfo pageInfo, QueryWrapper queryWrapper) {
        // init page params
        pageInfo.init();

        List<T> records;
        PageOrList<T> pageOrList;

        if (queryWrapper == null) {
            queryWrapper = new QueryWrapper();
        }

        PageType pageType = pageInfo.getPageType();

        if (pageType.equals(PageType.LIST)) {
            // query by list
            records = this.selectListByQuery(queryWrapper);
            pageOrList = new PageOrList(records);
        } else if (pageType.equals(PageType.PAGE)) {
            // query by paginate
            Page page = this.paginate(pageInfo.toPage(), queryWrapper);

            pageOrList = new PageOrList(page);
        } else if (pageType.equals(PageType.NEXT_TOKEN)) {
            // query by nextToken
            // decode nextToken
            if (VerifyUtils.isNotEmpty(pageInfo.getNextToken())) {
                NextToken nextToken = NextToken.decode(pageInfo.getNextToken());
                if (nextToken.getOperation().equals("0")) {
                    queryWrapper.le(nextToken.getColumn(), nextToken.getValue());
                } else {
                    queryWrapper.ge(nextToken.getColumn(), nextToken.getValue());
                }
            }

            // init page params by nextToken
            pageInfo.setPageNumber(1L);
            pageInfo.setPageSize(pageInfo.getPageSize() + 1);
            pageInfo.setTotalRow(1L);
            Page page = this.paginate(pageInfo.toPage(), queryWrapper);

            List<T> pageRecords = page.getRecords();

            // generate new nextToken
            String newNextToken = null;
            boolean hasMore = page.getRecords().size() >= pageInfo.getPageSize();
            if (hasMore) {
                Object lastRecords = pageRecords.get(pageRecords.size() - 1);
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode node = objectMapper.valueToTree(lastRecords);
                String queryValue = node.get(PighandFrameworkConfig.page.getNextColumn()).asText();

                newNextToken = NextToken.encode(queryValue);

                pageRecords.remove(page.getRecords().size() - 1);
            }

            pageOrList = new PageOrList(pageRecords, newNextToken);
        } else {
            throw new RuntimeException("分页类型错误");
        }

        return pageOrList;
    }

}
