package com.listore.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.listore.pojo.Cart;

public interface CartMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table listore_cart
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table listore_cart
     *
     * @mbg.generated
     */
    int insert(Cart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table listore_cart
     *
     * @mbg.generated
     */
    int insertSelective(Cart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table listore_cart
     *
     * @mbg.generated
     */
    Cart selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table listore_cart
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(Cart record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table listore_cart
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(Cart record);

	Cart selectByProductIdUserId(@Param("userId")Integer userId, @Param("productId")Integer productId);

    List<Cart> selectByUserId(Integer userId);

    int selectCartProductCheckedByUserId(Integer userId);

    int deleteByUserIdProductIds(@Param("userId")Integer userId,@Param("productIdList")List<String> productIdList);


    int selectOrUnSelectByUserIdProductIds(@Param("userId")Integer userId, @Param("productId") Integer productId, @Param("checked")Integer checked);

    int selectProductCountByUserId(Integer userId);

    List<Cart> selectCheckedByUserId(Integer userId);
}