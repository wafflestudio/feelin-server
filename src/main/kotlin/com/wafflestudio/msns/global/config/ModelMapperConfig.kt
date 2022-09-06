package com.wafflestudio.msns.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.modelmapper.ModelMapper

@Configuration
class ModelMapperConfig {
    @Bean
    fun modelMapper(): ModelMapper {
        val modelMapper = ModelMapper()
        modelMapper.configuration.isFieldMatchingEnabled = true
        modelMapper.configuration.fieldAccessLevel = org.modelmapper.config.Configuration.AccessLevel.PRIVATE
        return modelMapper
    }
}
