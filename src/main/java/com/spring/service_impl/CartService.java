package com.spring.service_impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
	private ICartRepository repository;;


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
        genericDao.create(cart);
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
                ResponseEntity<LmsResponse> response = getCourseById(cart.getProductId());
                if (response.getBody() != null) {
                    Map<String, Object> list = response.getBody().getResponse();
                    String[] keys = {"courseName", "userName", "courseCategory", "rating", "courseLevel", "totalDuration", "totalChapters", "image"};
                    for (String key : keys) {
                        cartDtoList.put(key, list.get(key));
                    }
                    cartDtoList.put("courseId", list.get("courseId"));
                    cartDtoList.put("price", cart.getPrice());
                    cartDtoList.put("userId", cart.getUserId());
                    cartDtoList.put("id", cart.getId());
                    mapList.add(cartDtoList);
                }
			}
        } catch (Exception e) {
            // Handle exception
        }
		  
		return mapList;
	}

	@Override
	@Transactional
	public void removeItemFromCart(int cartId) {
		// TODO Auto-generated method stub
		try {
			 if (!repository.existsById(cartId))
		            throw new CartItemNotExistException("Cart id is invalid : " + cartId);
		      //  cartRepository.deleteById(id);
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
