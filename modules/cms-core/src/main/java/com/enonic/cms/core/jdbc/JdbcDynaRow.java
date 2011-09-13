package com.enonic.cms.core.jdbc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

public final class JdbcDynaRow
{
    private final static DateFormat DATE_FORMAT =
        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private final Map<String, Object> data;

    public JdbcDynaRow(final Map<String, Object> data)
    {
        this.data = data;
    }

    public String getString(final String name)
    {
        return convertToString(getObject(name));
    }

    public String getString(final String name, final String defValue)
    {
        final String value = getString(name);
        return value != null ? value : defValue;
    }

    public Integer getInteger(final String name)
    {
        return convertToInteger(getObject(name));
    }

    public Integer getInteger(final String name, final Integer defValue)
    {
        final Integer value = getInteger(name);
        return value != null ? value : defValue;
    }

    public Long getLong(final String name)
    {
        return convertToLong(getObject(name));
    }

    public Long getLong(final String name, final Long defValue)
    {
        final Long value = getLong(name);
        return value != null ? value : defValue;
    }

    public Boolean getBoolean(final String name)
    {
        return convertToBoolean(getObject(name));
    }

    public Boolean getBoolean(final String name, final Boolean defValue)
    {
        final Boolean value = getBoolean(name);
        return value != null ? value : defValue;
    }

    public Date getDate(final String name)
    {
        return convertToDate(getObject(name));
    }

    public Date getDate(final String name, final Date defValue)
    {
        final Date value = getDate(name);
        return value != null ? value : defValue;
    }

    public byte[] getBytes(final String name)
    {
        return convertToBytes(getObject(name));
    }

    public byte[] getBytes(final String name, final byte[] defValue)
    {
        final byte[] value = getBytes(name);
        return value != null ? value : defValue;
    }

    public Object getObject(final String name)
    {
        String nameKey = (name == null) ? null : name.toLowerCase();
        if (!this.data.containsKey(nameKey)) {
            throw new IllegalArgumentException("Column [" + name + "] does not exist");
        }

        return this.data.get(nameKey);
    }

    public Object getObject(final String name, final Object defValue)
    {
        final Object value = getObject(name);
        return value != null ? value : defValue;
    }

    private String convertToString(final Object value)
    {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String)value;
        } else if (value instanceof Date) {
            return convertToString((Date)value);
        } else if (value instanceof byte[]) {
            return convertToString((byte[])value);
        } else {
            return value.toString();
        }
    }

    private Integer convertToInteger(final Object value)
    {
        if (value == null) {
            return null;
        } else if (value instanceof Integer) {
            return (Integer)value;
        } else if (value instanceof Number) {
            return ((Number)value).intValue();
        } else {
            throw new IllegalArgumentException("Cannot convert [" + value.getClass().getName() + "] to integer");
        }
    }

    private Long convertToLong(final Object value)
    {
        if (value == null) {
            return null;
        } else if (value instanceof Long) {
            return (Long)value;
        } else if (value instanceof Number) {
            return ((Number)value).longValue();
        } else if (value instanceof Date) {
            return ((Date)value).getTime();
        } else {
            throw new IllegalArgumentException("Cannot convert [" + value.getClass().getName() + "] to long");
        }
    }

    private Boolean convertToBoolean(final Object value)
    {
        if (value == null) {
            return null;
        } else if (value instanceof Boolean) {
            return (Boolean)value;
        } else if (value instanceof Number) {
            return ((Number)value).intValue() != 0;
        } else {
            throw new IllegalArgumentException("Cannot convert [" + value.getClass().getName() + "] to boolean");
        }
    }

    private byte[] convertToBytes(final Object value)
    {
        if (value == null) {
            return null;
        } else if (value instanceof byte[]) {
            return (byte[])value;
        } else if (value instanceof SerialBlob) {
            SerialBlob blob = (SerialBlob) value;
            try
            {
                return blob.getBytes( 1, (int) blob.length() );
            }
            catch ( SerialException e )
            {
                throw new IllegalArgumentException( "Cannot convert [" + value.getClass().getName() + "] to bytes", e );
            }
        } else {
            throw new IllegalArgumentException("Cannot convert [" + value.getClass().getName() + "] to bytes");
        }
    }

    private Date convertToDate(final Object value)
    {
        if (value == null) {
            return null;
        } else if (value instanceof Date) {
            return (Date)value;
        } else {
            throw new IllegalArgumentException("Cannot convert [" + value.getClass().getName() + "] to date");
        }
    }

    private String convertToString(final Date value)
    {
        return DATE_FORMAT.format(value);
    }

    private String convertToString(final byte[] value)
    {
        return "[" + value.length + " bytes]";
    }
}
