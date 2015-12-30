package com.rascal.core.pagination;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.service.spi.ServiceException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用于jqGrid自定义高级查询条件封装调教组合
 * <p>
 * Date: 2015/11/27
 * Time: 17:26
 *
 * @author Rascal
 */
public class GroupPropertyFilter {

    public static final String GROUP_OPERATION_AND = "and";
    public static final String GROUP_OPERATION_OR = "or";
    private static ObjectMapper objectMapper = new ObjectMapper();
    /*组合类型:AND/OR*/
    private String groupType = GROUP_OPERATION_AND;

    /*组合条件列表*/
    private List<PropertyFilter> filters = Lists.newArrayList();

    /*强制追加ADD条件列表*/
    private List<PropertyFilter> forceAndFilters = Lists.newArrayList();

    /*组合条件组*/
    private List<GroupPropertyFilter> groups = Lists.newArrayList();

    public GroupPropertyFilter() {
    }

    public GroupPropertyFilter(String groupType) {
        this.groupType = groupType;
    }

    public static GroupPropertyFilter buildFromHttpRequest(Class<?> entityClass, HttpServletRequest request) {
        try {
            String filtersJson = request.getParameter("filters");
            GroupPropertyFilter groupPropertyFilter = new GroupPropertyFilter();
            if (StringUtils.isNotBlank(filtersJson)) {
                JqGridSearchFilter jqFilter = objectMapper.readValue(filtersJson, JqGridSearchFilter.class);
                convertJqGridToFilter(entityClass, groupPropertyFilter, jqFilter);
            }

            List<PropertyFilter> filters = PropertyFilter.buildFiltersFromHttpRequest(entityClass, request);
            if (CollectionUtils.isNotEmpty(filters)) {
                GroupPropertyFilter comboGroupPropertyFilter = new GroupPropertyFilter();
                comboGroupPropertyFilter.setGroupType(GROUP_OPERATION_AND);

                GroupPropertyFilter normalGroupPropertyFilter = new GroupPropertyFilter();
                normalGroupPropertyFilter.setGroupType(GROUP_OPERATION_AND);
                normalGroupPropertyFilter.setFilters(filters);
                comboGroupPropertyFilter.getGroups().add(groupPropertyFilter);
                return comboGroupPropertyFilter;
            }

            return groupPropertyFilter;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    private static void convertJqGridToFilter(Class<?> entityClass, GroupPropertyFilter jqGroupPropertyFilter, JqGridSearchFilter jqFilter) {
        jqGroupPropertyFilter.setGroupType(jqFilter.getGroupOp().equalsIgnoreCase("OR") ? GROUP_OPERATION_OR : GROUP_OPERATION_AND);

        List<JqGridSearchRule> rules = jqFilter.getRules();
        List<PropertyFilter> filters = Lists.newArrayList();
        for (JqGridSearchRule rule : rules) {
            if (StringUtils.isBlank(rule.getData())) {
                continue;
            }
            PropertyFilter filter = new PropertyFilter(entityClass, rule.getOp().toUpperCase() + "_" + rule.getField(), rule.getData());
            filters.add(filter);
        }
        jqGroupPropertyFilter.setFilters(filters);

        List<JqGridSearchFilter> groups = jqFilter.getGroups();
        for (JqGridSearchFilter group : groups) {
            GroupPropertyFilter jqChildGroupPropertyFilter = new GroupPropertyFilter();
            jqGroupPropertyFilter.getGroups().add(jqChildGroupPropertyFilter);
            convertJqGridToFilter(entityClass, jqChildGroupPropertyFilter, group);
        }
    }

    public static GroupPropertyFilter buildDefaultAndGroupFilter(List<PropertyFilter> filters) {
        GroupPropertyFilter dpf = new GroupPropertyFilter(GROUP_OPERATION_AND);
        dpf.setFilters(filters);
        return dpf;
    }

    public static GroupPropertyFilter buildDefaultAndGroupFilter(PropertyFilter... filters) {
        GroupPropertyFilter dpf = new GroupPropertyFilter(GROUP_OPERATION_AND);
        if (filters != null && filters.length > 0) {
            dpf.setFilters(Lists.newArrayList(filters));
        }
        return dpf;
    }

    public static GroupPropertyFilter buildDefaultOrGroupFilter(PropertyFilter... filters) {
        GroupPropertyFilter dpf = new GroupPropertyFilter(GROUP_OPERATION_OR);
        if (filters != null && filters.length > 0) {
            dpf.setFilters(Lists.newArrayList(filters));
        }
        return dpf;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public List<PropertyFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<PropertyFilter> filters) {
        this.filters = filters;
    }

    public List<PropertyFilter> getForceAndFilters() {
        return forceAndFilters;
    }

    public void setForceAndFilters(List<PropertyFilter> forceAndFilters) {
        this.forceAndFilters = forceAndFilters;
    }

    public List<GroupPropertyFilter> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupPropertyFilter> groups) {
        this.groups = groups;
    }

    public GroupPropertyFilter append(GroupPropertyFilter... groups) {
        this.groups.addAll(Lists.newArrayList(groups));
        return this;
    }

    public GroupPropertyFilter append(PropertyFilter... filters) {
        this.filters.addAll(Lists.newArrayList(filters));
        return this;
    }

    public GroupPropertyFilter forceAnd(PropertyFilter... groups) {
        this.forceAndFilters.addAll(Lists.newArrayList(groups));
        return this;
    }

    /**
     * 判断当前是没有提供任何参数的默认查询
     * 一般用于父子结构类型数据根据无参数判断追加parent==null的查询条件
     */
    public boolean isEmptySearch() {
        if (CollectionUtils.isEmpty(filters) && CollectionUtils.isEmpty(groups)) {
            return true;
        }

        Set<PropertyFilter> mergeFileters = Sets.newHashSet();
        mergeFileters.addAll(filters);
        for (GroupPropertyFilter group : groups) {
            mergeFileters.addAll(group.getFilters());
            mergeFileters.addAll(group.getForceAndFilters());
        }
        for (PropertyFilter filter : mergeFileters) {
            //FETCH类型不算有效的查询条件，忽略掉
            if (!filter.getMatchType().equals(PropertyFilter.MatchType.FETCH)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 转换为key-Vaule的Map结构数据
     */
    public Map<String, Object> convertToMapParameters() {
        Map<String, Object> parameters = Maps.newHashMap();
        List<PropertyFilter> propertyFilters = Lists.newArrayList();
        propertyFilters.addAll(this.getFilters());
        if (CollectionUtils.isNotEmpty(this.getGroups())) {
            for (GroupPropertyFilter group : this.getGroups()) {
                if (CollectionUtils.isEmpty(group.getFilters()) && CollectionUtils.isEmpty(group.getForceAndFilters())) {
                    continue;
                }
                propertyFilters.addAll(group.getFilters());
            }
        }
        for (PropertyFilter propertyFilter : propertyFilters) {
            parameters.put(propertyFilter.getPropertyName(), propertyFilter.getMatchValue());
        }
        return parameters;
    }

    /**
     * filters={"groupOp":"AND", "rules":[{"field":"code","op":"eq","data":"123"}]}
     */
    public static class JqGridSearchFilter {
        private String groupOp;
        private List<JqGridSearchRule> rules = Lists.newArrayList();
        private List<JqGridSearchFilter> groups = Lists.newArrayList();

        public String getGroupOp() {
            return groupOp;
        }

        public void setGroupOp(String groupOp) {
            this.groupOp = groupOp;
        }

        public List<JqGridSearchRule> getRules() {
            return rules;
        }

        public void setRules(List<JqGridSearchRule> rules) {
            this.rules = rules;
        }

        public List<JqGridSearchFilter> getGroups() {
            return groups;
        }

        public void setGroups(List<JqGridSearchFilter> groups) {
            this.groups = groups;
        }
    }

    public static class JqGridSearchRule {
        private String field;

        /**
         * [
         * 'eq','ne','lt','le','gt','ge','bw','bn','in','ni','ew','en','cn','nc'
         * ] The corresponding texts are in language file and mean the
         * following: ['equal','not equal', 'less', 'less or
         * equal','greater','greater or equal', 'begins with','does not begin
         * with','is in','is not in','ends with','does not end
         * with','contains','does not contain']
         */
        private String op;
        private String data;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getOp() {
            return op;
        }

        public void setOp(String op) {
            this.op = op;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
