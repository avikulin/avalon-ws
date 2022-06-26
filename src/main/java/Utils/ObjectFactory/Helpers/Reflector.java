package Utils.ObjectFactory.Helpers;

import DAL.DataEntities.Enums.DeviceType;
import DAL.DataEntities.Enums.OsiLayer;
import Utils.ObjectFactory.Holders.RefHolder;

import java.lang.reflect.Field;
import java.util.Objects;

public class Reflector {
    public static Object getFieldValue(Field field, Object container){
        Objects.requireNonNull(field, "Не задано поле-владелец значения");
        Objects.requireNonNull(container, "Не задан объект-владелец значения");
        try {
            boolean accessMode = field.isAccessible();
            field.setAccessible(true);
            Object res = field.get(container);
            field.setAccessible(accessMode);
            return res;
        } catch (IllegalAccessException e) {
            String msg = String.format("Ошибка получения значения поля \"%s\" объекта \"%s\" : %s",
                    field.getName(),
                    container,
                    e.getMessage());
            throw new IllegalStateException(msg);
        }
    }

    public static void setFieldValue(Field field, Object container, Object value){
        Objects.requireNonNull(field, "Не задано поле-владелец значения");
        Objects.requireNonNull(container, "Не задан объект-владелец значения");
        Objects.requireNonNull(value, "Не задано присваиваемое значение");
        try {
            boolean isStatic = java.lang.reflect.Modifier.isStatic(field.getModifiers());
            boolean isProtected = java.lang.reflect.Modifier.isProtected(field.getModifiers());
            if (isStatic || isProtected) return;

            boolean accessMode = field.isAccessible();
            field.setAccessible(true);
            field.set(container, value);
            field.setAccessible(accessMode);
        } catch (IllegalAccessException e) {
            String msg = String.format("Ошибка задания значения для поля \"%s\" объекта \"%s\" : %s",
                    field.getName(),
                    container,
                    e.getMessage());
            throw new IllegalStateException(msg);
        }
    }

    public static void setFieldValue(RefHolder reference, Object value){
        Objects.requireNonNull(reference, "Не задано поле-владелец значения");
        Objects.requireNonNull(value, "Не задано присваиваемое значение");
        try {
            Field field = reference.getContainerFieldRef();
            boolean accessMode = field.isAccessible();
            field.setAccessible(true);
            field.set(reference.getContainerObject(), value);
            field.setAccessible(accessMode);
        } catch (IllegalAccessException e) {
            String msg = String.format("Ошибка задания значения для поля \"%s\" объекта \"%s\" класса \"%s\": %s",
                    reference.getContainerFieldRef().getName(),
                    reference.getContainerObject(),
                    reference.getContainerClass().getName(),
                    e.getMessage());
            throw new IllegalStateException(msg);
        }
    }

    public static Object getScalarValue(String s, Class<?> clazz){
        if (clazz.equals(String.class)) return s; // идентичное отображение

        if (clazz.equals(int.class) || clazz.equals(Integer.class)){
            return Integer.parseInt(s);
        }

        if (clazz.equals(long.class) || clazz.equals(Long.class)){
            return Long.parseLong(s);
        }

        if (clazz.equals(double.class) || clazz.equals(Double.class)){
            return Double.parseDouble(s);
        }

        if (clazz.equals(OsiLayer.class)){
            return OsiLayer.valueOf(s);
        }

        if (clazz.equals(DeviceType.class)){
            return DeviceType.valueOf(s);
        }

        String msg = String.format("Неподдерживаемый скалярный тип \"%s\" в значении \"%s\"", clazz.getName(), s);
        throw new IllegalStateException(msg);
    }
}
