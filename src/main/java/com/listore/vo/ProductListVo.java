package com.listore.vo;

import java.math.BigDecimal;

/*
 * 
 * 由于返回给用户一些有用 的信息就行了，那些没有用的信息不返回所以在这里对产品对象进行封装操作
 * Value Object
 * 展示给用户的对象
 * */
public class ProductListVo {
	    private int id;
	    private int categoryId;
	    //产品名字
		private String name;
		//产品子标题
		private String subtitle;
		//产品主图
		private String mainImage;
		//产品价格
		private BigDecimal price;
		//产品状态
		private int status;
		//产品图片的host
		private String imageHost;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public int getCategoryId() {
			return categoryId;
		}
		public void setCategoryId(int categoryId) {
			this.categoryId = categoryId;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getSubtitle() {
			return subtitle;
		}
		public void setSubtitle(String subtitle) {
			this.subtitle = subtitle;
		}
		public String getMainImage() {
			return mainImage;
		}
		public void setMainImage(String mainImage) {
			this.mainImage = mainImage;
		}
		public BigDecimal getPrice() {
			return price;
		}
		public void setPrice(BigDecimal price) {
			this.price = price;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public String getImageHost() {
			return imageHost;
		}
		public void setImageHost(String imageHost) {
			this.imageHost = imageHost;
		}
		
		
		
}
