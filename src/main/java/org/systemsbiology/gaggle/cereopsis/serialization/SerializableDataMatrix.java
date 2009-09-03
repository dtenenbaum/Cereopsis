package org.systemsbiology.gaggle.cereopsis.serialization;


import org.systemsbiology.gaggle.core.datatypes.DataMatrix;

/*
* Copyright (C) 2008 by Institute for Systems Biology,
* Seattle, Washington, USA.  All rights reserved.
*
* This source code is distributed under the GNU Lesser
* General Public License, the text of which is available at:
*   http://www.gnu.org/copyleft/lesser.html
*/
public class SerializableDataMatrix extends DataMatrix {

    public SerializableDataMatrix() {
    }

    public SerializableDataMatrix(DataMatrix matrix) {
        super.name = matrix.getName();
        super.metadata = matrix.getMetadata();
        super.species = matrix.getSpecies();
        super.rowTitlesTitle = matrix.getRowTitlesTitle();
        super.columnTitles = matrix.getColumnTitles();
        super.rowTitles = matrix.getRowTitles();
        super.data = matrix.get();
        //super.uri
        super.fullName = matrix.getFullName();
        super.fileExtension = matrix.getFileExtension();
        super.dataTypeBriefName = matrix.getDataTypeBriefName();

    }


    public DataMatrix toDataMatrix() {
        DataMatrix matrix = new DataMatrix();
        matrix.setName(name);
        matrix.setMetadata(metadata);
        matrix.setSpecies(species);
        matrix.setRowTitlesTitle(rowTitlesTitle);
        matrix.setColumnTitles(columnTitles);
        matrix.setRowTitles(rowTitles);
        matrix.set(data);
        matrix.setFullName(fullName);
        //can't set file extension
        matrix.setDataTypeBriefName(dataTypeBriefName);
        return matrix;
    }

    public void setData(double[][] data) {
        super.set(data);
    }

    public double[][] getData() {
        return super.get();
    }

}
