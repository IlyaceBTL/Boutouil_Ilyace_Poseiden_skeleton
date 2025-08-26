package com.nnk.springboot.services;

import com.nnk.springboot.domain.CurvePoint;
import java.util.List;

public interface CurveService {
    List<CurvePoint> findAll();
    CurvePoint findById(Integer id);
    CurvePoint create(CurvePoint curvePoint);
    CurvePoint update(Integer id, CurvePoint curvePoint);
    void delete(Integer id);
}
