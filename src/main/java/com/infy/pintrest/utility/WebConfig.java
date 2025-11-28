//package com.infy.pintrest.utility;
//
//public class WebConfig {
//
//}

package com.infy.pintrest.utility;



import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



public class WebConfig implements WebMvcConfigurer{

@Override

public void addResourceHandlers(ResourceHandlerRegistry registery) {

registery.addResourceHandler("/board-covers/**").addResourceLocations("file:board-covers/");

registery.addResourceHandler("/profile-uploads/**").addResourceLocations("file:profile-uploads/");

registery.addResourceHandler("/pins-uploads/**").addResourceLocations("file:pins-uploads/");

registery.addResourceHandler("/ads-uploads/**").addResourceLocations("file:ads-uploads/");

}

}

