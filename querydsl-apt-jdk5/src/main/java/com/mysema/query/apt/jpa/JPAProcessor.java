/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.query.apt.jpa;

import static com.mysema.query.apt.Constants.JPA_EMBEDDABLE;
import static com.mysema.query.apt.Constants.JPA_ENTITY;
import static com.mysema.query.apt.Constants.JPA_SUPERCLASS;
import static com.mysema.query.apt.Constants.QD_DTO;
import static com.sun.mirror.util.DeclarationVisitors.NO_OP;
import static com.sun.mirror.util.DeclarationVisitors.getDeclarationScanner;

import java.util.Map;

import com.mysema.query.apt.general.DefaultEntityVisitor;
import com.mysema.query.apt.general.GeneralProcessor;
import com.mysema.query.codegen.Serializers;
import com.mysema.query.codegen.ClassModel;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MethodDeclaration;

/**
 * JPAProcessor provides JPA annotation handling support
 * 
 * @author tiwe
 * @version $Id$
 */
public class JPAProcessor extends GeneralProcessor {

    public JPAProcessor(AnnotationProcessorEnvironment env) {
        super(env, JPA_SUPERCLASS, JPA_ENTITY, QD_DTO);
    }

    private void createEmbeddableClasses() {
        DefaultEntityVisitor entityVisitor = new DefaultEntityVisitor();
        AnnotationTypeDeclaration a = (AnnotationTypeDeclaration) env.getTypeDeclaration(JPA_EMBEDDABLE);
        for (Declaration typeDecl : env.getDeclarationsAnnotatedWith(a)) {
            typeDecl.accept(getDeclarationScanner(entityVisitor, NO_OP));
        }

        Map<String, ClassModel> entityTypes = entityVisitor.types;
        if (entityTypes.isEmpty()) {
            env.getMessager().printNotice("No class generation for embeddable types");
        } else {
            serializeAsOuterClasses(entityTypes.values(), Serializers.EMBEDDABLE);
        }

    }

    // TODO : add switch for field / getter handling
    @Override
    protected DefaultEntityVisitor createEntityVisitor() {
        return new DefaultEntityVisitor() {
            @Override
            public void visitMethodDeclaration(MethodDeclaration d) {
                // skip property handling
            }
        };
    }

    public void process() {
        super.process();
        createEmbeddableClasses();
    }
}
