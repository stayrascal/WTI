package com.rascal.core.validation.impl;

import com.rascal.core.validation.FormattedDate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormattedDateValidator implements ConstraintValidator<FormattedDate, Object> {

    String pattern;

    @Override
    public void initialize(FormattedDate formattedDate) {
        this.pattern = formattedDate.pattern();
        if (StringUtils.isBlank(this.pattern)) {
            switch (formattedDate.iso()) {
            case DATE:
                this.pattern = "yyyy-MM-dd";
                break;
            case TIME:
                this.pattern = "hh:mm:ss";
                break;
            case DATE_TIME:
                this.pattern = "yyyy-MM-dd hh:mm:ss";
                break;
            default:
                break;
            }
        }
        Assert.notNull(this.pattern);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        String data = String.valueOf(value);
        return StringUtils.isBlank(data) || isDate(data, pattern);
    }

    private static boolean isDate(String value, String format) {
        SimpleDateFormat sdf = null;
        ParsePosition pos = new ParsePosition(0);//指定从所传字符串的首位开始解析  
        if (StringUtils.isBlank(value)) {
            return false;
        }
        try {
            sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            Date date = sdf.parse(value, pos);
            if (date == null) {
                return false;
            } else {
                //更为严谨的日期,如2011-03-024认为是不合法的  
                return pos.getIndex() <= sdf.format(date).length();
            }
        } catch (Exception e) {
            return false;
        }
    }
}
