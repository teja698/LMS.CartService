package com.spring.service_impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.BaseDao.IGenericDao;
import com.spring.config.RecordNotFoundException;
import com.spring.dto.AddItem;
import com.spring.dto.LmsResponse;
import com.spring.exception.CartItemNotExistException;
import com.spring.iservice.ICartService;
import com.spring.model.Cart;
import com.spring.repository.ICartRepository;
import com.spring.service.BaseServiceImpl;

@Service
public class CartService extends BaseServiceImpl implements ICartService {
	@Autowired
	private ICartRepository repository;
        private IGenericDao<Cart> cart1 ;
        	static Logger logger = Logger.getLogger(CartService.class);

	@Override
	@Transactional
	public Cart addToCart(AddItem addCart,int userId){
		Cart cart=new Cart();
    try {
        cart.setProductId(addCart.getProductId());
        cart.setUserId(userId);
        cart.setPrice(addCart.getPrice());
        cart.setCreatedDate(new Date());
        cart.setIsActive(true);
        cart1.create(cart);
    } catch (Exception e) {
        throw e;
    }
    return cart;
	}
	@Override
	@Transactional
	public List<Map<String, Object>> getAllItemsInCart(int userId) {
		List<Map<String, Object>> mapList=new ArrayList<>();
		
        try {
           List<Cart> cartList = repository.getAllList(userId);
            for (Cart cart : cartList) {
                Map<String, Object> cartDtoList = new HashMap<>();
                LmsResponse response = getCourseById(cart.getProductId()).getBody();
                if (response!= null ) {
                 Map<String, Object> list = response.getResponse();

                    String[] keys = {"courseName", "userName", "courseCategory", "rating", "courseLevel", "totalDuration", "totalChapters", "image"};
                    for (String key : keys) {
                        cartDtoList.put(key, list.get(key));
                    }
                    cartDtoList.put("courseId", list.get("courseId"));
                    cartDtoList.put("price", cart.getPrice());
                    cartDtoList.put("userId", cart.getUserId());
                    cartDtoList.put("id", cart.getId());
                    mapList.add(cartDtoList);
                }else{
                    throw new RecordNotFoundException("Record not found for course id : " + cart.getProductId() + " and user id : " + userId);

                }
                
            }
        } catch (Exception e) {
            throw e;
        }
		  
		return mapList;
	}

	@Override
	@Transactional
	public void removeItemFromCart(int cartId) {
		try {
			 if (!repository.existsById(cartId))
		            throw new CartItemNotExistException("Cart id is invalid : " + cartId);
			repository.removeItemFromCart(cartId);
		}
		catch (Exception e) {
			throw e;
		}
	}
	@Override
	@Transactional
	public void deleteCartItems(int userId) {
        repository.deleteAllItemsInCart(userId);
    }
}
