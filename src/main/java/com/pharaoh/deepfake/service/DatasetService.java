package com.pharaoh.deepfake.service;

import com.pharaoh.deepfake.common.util.R;
import com.pharaoh.deepfake.controller.form.DatasetForm;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;

public interface DatasetService {

    public R setDataset(DatasetForm datasetForm);
    public List findDataset(String datasetType);
    public HashMap listVideoDatasetPage(Pageable pageable, String datasetName, String filename );
    public Resource loadFileAsResource(String datasetName, String filename, String datasetType);

}
