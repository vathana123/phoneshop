package com.backend.phoneshop.controller;

import com.backend.phoneshop.dto.data.BrandDto;
import com.backend.phoneshop.dto.respone.PageResponse;
import com.backend.phoneshop.service.BrandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BrandController.class)
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BrandService brandService;

    @Test
    @DisplayName("Should return all brands")
    void getAll_ShouldReturnBrands() throws Exception {

        PageResponse<BrandDto> response = PageResponse.<BrandDto>builder()
                .content(List.of(
                        BrandDto.builder()
                                .id(1L)
                                .name("Apple")
                                .build(),
                        BrandDto.builder()
                                .id(2L)
                                .name("Samsung")
                                .build()
                ))
                .page(0)
                .size(10)
                .totalElements(2)
                .totalPages(1)
                .hasNext(false)
                .hasPrevious(false)
                .isFirst(true)
                .isLast(true)
                .build();

        Mockito.when(brandService.findAll(anyMap(), any(PageRequest.class)))
                .thenReturn(response);

        mockMvc.perform(get("/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Apple"))
                .andExpect(jsonPath("$.content[1].name").value("Samsung"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("Should return brand by id")
    void getById_ShouldReturnBrand() throws Exception {

        BrandDto dto = BrandDto.builder()
                .id(1L)
                .name("Apple")
                .build();

        Mockito.when(brandService.findById(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/brands/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Apple"));
    }

    @Test
    @DisplayName("Should create brand")
    void create_ShouldCreateBrand() throws Exception {

        BrandDto dto = BrandDto.builder()
                .id(1L)
                .name("Apple")
                .build();

        Mockito.when(brandService.save(any()))
                .thenReturn(dto);

        String request = """
                {
                    "name": "Apple"
                }
                """;

        mockMvc.perform(post("/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Apple"));
    }

    @Test
    @DisplayName("Should update brand")
    void update_ShouldUpdateBrand() throws Exception {

        BrandDto dto = BrandDto.builder()
                .id(1L)
                .name("Updated Apple")
                .build();

        Mockito.when(brandService.update(Mockito.eq(1L), any()))
                .thenReturn(dto);

        String request = """
                {
                    "name": "Updated Apple"
                }
                """;

        mockMvc.perform(put("/brands/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Apple"));
    }

    @Test
    @DisplayName("Should delete brand")
    void delete_ShouldDeleteBrand() throws Exception {

        Mockito.doNothing()
                .when(brandService)
                .delete(1L);

        mockMvc.perform(delete("/brands/1"))
                .andExpect(status().isOk());
    }
}