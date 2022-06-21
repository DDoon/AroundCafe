package com.example.demo.mypage.cafe.service.menu;

import com.example.demo.mypage.cafe.dto.CafeMenuDto;
import com.example.demo.mypage.cafe.entity.Cafe;
import com.example.demo.mypage.cafe.entity.CafeMenu;
import com.example.demo.mypage.cafe.repository.cafe.CafeRepository;
import com.example.demo.mypage.cafe.repository.menu.MenuRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MenuServiceImpl implements MenuService{

    @Autowired
    MenuRepository repository;

    @Autowired
    CafeRepository cafeRepository;


    @Transactional
    @Override
    public void includeImgSave(CafeMenuDto info, String fileName) {

        log.info("file: " +fileName);
        log.info("cafe no : " +info.getCafe_no());

        Cafe cafe = cafeRepository.findById(info.getCafe_no()).orElseGet(null);

        CafeMenu cafeMenu = CafeMenu.builder()
                .menu_name(info.getMenu_name())
                .menu_price(info.getMenu_price())
                .file(fileName)
                .menu_content(info.getMenu_content())
                .cafe(cafe)
                .build();

        repository.save(cafeMenu);

    }

    @Transactional
    @Override
    public void exceptImgSave(CafeMenuDto info) {
        log.info("cafe no : " +info.getCafe_no());

        Cafe cafe = cafeRepository.findById(info.getCafe_no()).orElseGet(null);

        CafeMenu cafeMenu = CafeMenu.builder()
                .menu_name(info.getMenu_name())
                .menu_price(info.getMenu_price())
                .menu_content(info.getMenu_content())
                .cafe(cafe)
                .build();

        repository.save(cafeMenu);
    }

    @Transactional
    @Override
    public void includeImgModify(CafeMenu info, String menu_img, Integer cafeNo) throws IOException {

        log.info("include img modify");
        log.info("file: " +menu_img);
        log.info("cafe no : " + cafeNo);

        Cafe cafe = cafeRepository.findById(Long.valueOf(cafeNo)).orElseGet(null);
        CafeMenu cafeMenu = repository.findById(info.getMenu_no()).orElseGet(null);

        if(cafeMenu.getMenu_img() != null) {
            Path filePath = Paths.get("../../cafefront/around_cafe/src/assets/cafe/cafeMenu/" + cafeMenu.getMenu_img());
            Files.delete(filePath);
            log.info("file delete complete");
        }

        log.info("!!!modify ok!!!!");
        info.setMenu_img(menu_img);
        info.setCafe(cafe);

        repository.save(info);
    }

    @Transactional
    @Override
    public void exceptImgModify(CafeMenu info, Integer cafeNo) {
        log.info("except img modify");

        Cafe cafe = cafeRepository.findById(Long.valueOf(cafeNo)).orElseGet(null);
        CafeMenu cafeMenu = repository.findById(info.getMenu_no()).orElseGet(null);

        if(cafeMenu.getMenu_img() != null) {
            info.setMenu_img(cafeMenu.getMenu_img());
        }

        log.info("!!!modify ok!!!!");

        info.setCafe(cafe);
        repository.save(info);

    }

    @Transactional
    @Override
    public List<CafeMenu> list(Integer membNo) {
        Cafe cafe = cafeRepository.findByMemberNo(Long.valueOf(membNo)).orElseGet(null);
        Long CafeNo = cafe.getCafeNo();
        log.info("cafe no = " + CafeNo);

        return repository.findByCafeNo(CafeNo);
    }

    @Transactional
    @Override
    public void delete(Integer menuNo) throws IOException {
        CafeMenu menu = repository.findById(Long.valueOf(menuNo)).orElseGet(null);

        if(menu.getMenu_img() != null) {
            Path file = Paths.get("../../cafefront/around_cafe/src/assets/cafe/cafeMenu/" + menu.getMenu_img());
            Files.delete(file);
        }

        repository.deleteById(Long.valueOf(menuNo));
    }

    @Transactional
    @Override
    public List<CafeMenu> list1() {
        log.info("test get list menu");
        return repository.findAll();
    }

    @Transactional
    @Override
    public String changeSignature(Integer menuNo) {

        Integer countSignature = repository.countSignature();
        
        if(countSignature >=2) {
            return "시그니처가 두개가 넘었습니다.";
        }else {
            CafeMenu menu = repository.findById(Long.valueOf(menuNo)).orElseGet(null);
            
            menu.setSignature(true);
            repository.save(menu);
            return "시그니처가 등록되었습니다.";
        }
        
    }
    
    @Transactional
    @Override
    public String deleteSignature(Integer menuNo) {
        
        CafeMenu menu = repository.findById(Long.valueOf(menuNo)).orElseGet(null);

        menu.setSignature(false);
        repository.save(menu);
        return "시그니처가 삭제되었습니다.";
        
    }

    @Override
    public String changeSoldOut(Integer menuNo) {
        CafeMenu menu = repository.findById(Long.valueOf(menuNo)).orElseGet(null);

        menu.setSold_out(true);
        repository.save(menu);
        return "솔드아웃 메뉴로 등록되었습니다.";
        
    }

    @Override
    public String deleteSoldOut(Integer menuNo) {
        CafeMenu menu = repository.findById(Long.valueOf(menuNo)).orElseGet(null);

        menu.setSold_out(false);
        repository.save(menu);
        return "솔드아웃 메뉴가 삭제되었습니다.";
    }

    @Override
    public List<CafeMenu> soldList() {
        log.info("sold out list");
        return repository.findBySoldTrue();
    }

    @Override
    public List<CafeMenu> sigList() {
        log.info("signature list");
        return repository.findBySignatureTrue();
    }

    

}
