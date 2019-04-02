package com.dimkov.bgMountains.web.controllers;

import com.dimkov.bgMountains.domain.models.binding.MountainRegisterBindingModel;
import com.dimkov.bgMountains.domain.models.service.MountainServiceModel;
import com.dimkov.bgMountains.domain.models.view.MountainDetailsViewModel;
import com.dimkov.bgMountains.domain.models.view.MountainViewModel;
import com.dimkov.bgMountains.service.CloudinaryService;
import com.dimkov.bgMountains.service.MountainService;
import com.dimkov.bgMountains.util.Constants;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mountains")
public class MountainController extends BaseController {
    private static final String NOT_FOUND_MESSAGE = "Mountain not found!";

    private static final String MOUNTAINS_VIEW = "mountain/mountains-home";
    private static final String ADD_MOUNTAIN_VIEW = "mountain/mountain-add";
    private static final String DETAILS_MOUNTAIN_VIEW = "mountain/mountain-details";

    private static final String ADD_MOUNTAIN_PATH = "/mountains/add";
    private static final String ALL_MOUNTAINS_PATH = "/mountains";

    private final ModelMapper modelMapper;
    private final MountainService mountainService;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public MountainController(ModelMapper modelMapper,
                              MountainService mountainService,
                              CloudinaryService cloudinaryService) {
        this.modelMapper = modelMapper;
        this.mountainService = mountainService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping
    public ModelAndView mountainHome(ModelAndView modelAndView) {
        List<MountainViewModel> mountains = this.mountainService.findAll()
                .stream()
                .map(m -> this.modelMapper.map(m, MountainViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject(Constants.MODEL_ATTR_NAME, mountains);

        return view(MOUNTAINS_VIEW, modelAndView);
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority(T(com.dimkov.bgMountains.util.Constants).ROLE_MODERATOR)")
    public ModelAndView showAddForm() {
        return view(ADD_MOUNTAIN_VIEW);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority(T(com.dimkov.bgMountains.util.Constants).ROLE_MODERATOR)")
    public ModelAndView addMountain(@Valid @ModelAttribute MountainRegisterBindingModel mountainRegisterBindingModel) throws IOException {
        MountainServiceModel mountain = this.modelMapper.map(mountainRegisterBindingModel, MountainServiceModel.class);

        mountain.setImageUrl(
                cloudinaryService.uploadImage(mountainRegisterBindingModel.getImage())
        );

        if (!this.mountainService.save(mountain)) {
            return redirect(ADD_MOUNTAIN_PATH);
        }

        return redirect(ALL_MOUNTAINS_PATH);
    }

    @GetMapping("/details/{id}")
    public ModelAndView showMountainDetails(@PathVariable String id, ModelAndView modelAndView) {
        MountainServiceModel foundMountain = this.mountainService.findById(id)
                .orElseThrow(() -> new NoSuchElementException(NOT_FOUND_MESSAGE));

        modelAndView.addObject(Constants.MODEL_ATTR_NAME, this.modelMapper.map(foundMountain, MountainDetailsViewModel.class));

        return view(DETAILS_MOUNTAIN_VIEW,modelAndView);
    }
}
