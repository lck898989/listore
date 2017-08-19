package com.listore.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.listore.pojo.Product;
/*
 * 这里传递多个参数的话要加上一个@param注解
 * 作用将参数命名供配置文件识别参数的名字不然会报
 * org.apache.ibatis.binding.BindingException: Parameter 'status' not found. Available parameters are [a
 * 异常
 * */
public interface ProductMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table listore_product
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table listore_product
     *
     * @mbg.generated
     */
    int insert(Product record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table listore_product
     *
     * @mbg.generated
     */
    int insertSelective(Product record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table listore_product
     *
     * @mbg.generated
     */
    Product selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table listore_product
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(Product record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table listore_product
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Product record);
    /*
     * 把所有的记录显示出来
     * 
     * */
    List<Product> select();

	int updateSaleStatusById(@Param("productId")int productId,@Param("status")int status);

	List<Product> selectByProductNameAndProductId(@Param("productName")String productName, @Param("productId")int productId);
    List<Product> selectByProductName(@Param("productName")String productNam,@Param("categoryId")Integer categoryId);
}