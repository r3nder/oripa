/**
 * ORIPA - Origami Pattern Editor 
 * Copyright (C) 2013-     ORIPA OSS Project  https://github.com/oripa/oripa
 * Copyright (C) 2005-2009 Jun Mitani         http://mitani.cs.tsukuba.ac.jp/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package oripa.persistent.entity.exporter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import javax.vecmath.Vector2d;

import oripa.domain.fold.OriFace;
import oripa.domain.fold.OriHalfedge;
import oripa.domain.fold.OrigamiModel;
import oripa.persistent.doc.Exporter;

/**
 * @author Koji
 * 
 */
public class OrigamiModelExporterDXF implements Exporter<OrigamiModel> {

	/*
	 * (non Javadoc)
	 * 
	 * @see oripa.persistent.doc.Exporter#export(java.lang.Object,
	 * java.lang.String)
	 */
	@Override
	public boolean export(final OrigamiModel origamiModel, final String filePath) throws Exception {
		double paperSize = origamiModel.getPaperSize();

		double scale = 6.0 / paperSize; // 6.0 inch width
		double center = 4.0; // inch
		FileWriter fw = new FileWriter(filePath);
		BufferedWriter bw = new BufferedWriter(fw);

		// Align the center of the model, combine scales
		Vector2d maxV = new Vector2d(-Double.MAX_VALUE, -Double.MAX_VALUE);
		Vector2d minV = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);
		Vector2d modelCenter = new Vector2d();

		List<OriFace> faces = origamiModel.getFaces();
		List<OriFace> sortedFaces = origamiModel.getSortedFaces();

		for (OriFace face : faces) {
			for (OriHalfedge he : face.halfedges) {
				maxV.x = Math.max(maxV.x, he.vertex.p.x);
				maxV.y = Math.max(maxV.y, he.vertex.p.y);
				minV.x = Math.min(minV.x, he.vertex.p.x);
				minV.y = Math.min(minV.y, he.vertex.p.y);
			}
		}

		modelCenter.x = (maxV.x + minV.x) / 2;
		modelCenter.y = (maxV.y + minV.y) / 2;

		bw.write("  0\n");
		bw.write("SECTION\n");
		bw.write("  2\n");
		bw.write("HEADER\n");
		bw.write("  9\n");
		bw.write("$ACADVER\n");
		bw.write("  1\n");
		bw.write("AC1009\n");
		bw.write("  0\n");
		bw.write("ENDSEC\n");
		bw.write("  0\n");
		bw.write("SECTION\n");
		bw.write("  2\n");
		bw.write("ENTITIES\n");

		for (OriFace face : sortedFaces) {
			for (OriHalfedge he : face.halfedges) {

				bw.write("  0\n");
				bw.write("LINE\n");
				bw.write("  8\n");
				bw.write("_0-0_\n"); // Layer name
				bw.write("  6\n");
				bw.write("CONTINUOUS\n"); // Line type
				bw.write(" 62\n"); // 1＝red 2＝yellow 3＝green 4＝cyan 5＝blue
									// 6＝magenta 7＝white
				int colorNumber = 250;

				bw.write("" + colorNumber + "\n");
				bw.write(" 10\n");
				bw.write(""
						+ ((he.positionForDisplay.x - modelCenter.x)
								* scale + center) + "\n");
				bw.write(" 20\n");
				bw.write(""
						+ (-(he.positionForDisplay.y - modelCenter.y)
								* scale + center) + "\n");
				bw.write(" 11\n");
				bw.write(""
						+ ((he.next.positionForDisplay.x - modelCenter.x)
								* scale + center) + "\n");
				bw.write(" 21\n");
				bw.write(""
						+ (-(he.next.positionForDisplay.y - modelCenter.y)
								* scale + center) + "\n");
			}
		}

		bw.write("  0\n");
		bw.write("ENDSEC\n");
		bw.write("  0\n");
		bw.write("EOF\n");

		bw.close();

		return true;
	}
}
