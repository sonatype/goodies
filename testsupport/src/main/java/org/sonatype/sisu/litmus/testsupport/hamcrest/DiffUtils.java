/*
 * Copyright (c) 2007-2013 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

package org.sonatype.sisu.litmus.testsupport.hamcrest;

import java.util.List;

import com.google.common.base.Strings;
import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;

/**
 * Some ideas to represent test assertions in more usable way
 * <p/>
 * Inspired and blatantly copied from:
 * http://stackoverflow.com/questions/319479/generate-formatted-diff-output-in-java
 * <p/>
 * Thank you!
 *
 * @since 1.0
 */
public class DiffUtils
{


  /**
   * Returns a visual diff between two strings (text file like strings).
   *
   * @since 1.0
   */
  public static String diffSideBySide(String fromStr, String toStr, boolean onlyDiffs) {
    // this is equivalent of running unix diff -y command
    // not pretty, but it works. Feel free to refactor against unit test.
    String[] fromLines = fromStr == null ? new String[0] : fromStr.split("\n");
    String[] toLines = toStr == null ? new String[0] : toStr.split("\n");

    int widthOfLineNum = String.valueOf(Math.max(fromLines.length, toLines.length)).length();
    String lnf = "%0" + widthOfLineNum + "d: ";
    String emptyLineNumber = Strings.padStart("", lineNumber(lnf, 0).length(), ' ');

    List<Difference> diffs = new Diff<String>(fromLines, toLines).diff();

    int padding = 3;
    int maxStrWidth = Math.max(maxLength(fromLines), maxLength(toLines)) + padding;

    StringBuilder diffOut = new StringBuilder();

    int fromLineNum = 0;
    int toLineNum = 0;
    for (Difference diff : diffs) {
      int delStart = diff.getDeletedStart();
      int delEnd = diff.getDeletedEnd();
      int addStart = diff.getAddedStart();
      int addEnd = diff.getAddedEnd();

      boolean isAdd = (delEnd == Difference.NONE && addEnd != Difference.NONE);
      boolean isDel = (addEnd == Difference.NONE && delEnd != Difference.NONE);
      boolean isMod = (delEnd != Difference.NONE && addEnd != Difference.NONE);

      //write out unchanged lines between diffs
      while (true) {
        String left = "";
        String right = "";
        if (fromLineNum < (delStart)) {
          left = fromLines[fromLineNum];
          fromLineNum++;
        }
        if (toLineNum < (addStart)) {
          right = toLines[toLineNum];
          toLineNum++;
        }

        if (!onlyDiffs && fromLineNum > 0 && toLineNum > 0) {
          diffOut.append("    ");
          diffOut.append(lineNumber(lnf, fromLineNum));
          diffOut.append(Strings.padEnd(left, maxStrWidth, ' '));
          diffOut.append("  "); // no operator to display
          diffOut.append(lineNumber(lnf, toLineNum));
          diffOut.append(right).append("\n");
        }

        if ((fromLineNum == (delStart)) && (toLineNum == (addStart))) {
          break;
        }
      }

      if (isDel) {
        //write out a deletion
        for (int i = delStart; i <= delEnd; i++) {
          fromLineNum++;
          diffOut.append("[E] ");
          diffOut.append(lineNumber(lnf, fromLineNum));
          diffOut.append(Strings.padEnd(fromLines[i], maxStrWidth, ' '));
          diffOut.append("<").append("\n");
        }
      }
      else if (isAdd) {
        //write out an addition
        for (int i = addStart; i <= addEnd; i++) {
          toLineNum++;
          diffOut.append("[A] ");
          diffOut.append(emptyLineNumber);
          diffOut.append(Strings.padEnd("", maxStrWidth, ' '));
          diffOut.append("> ");
          diffOut.append(lineNumber(lnf, toLineNum));
          diffOut.append(toLines[i]).append("\n");
        }
      }
      else if (isMod) {
        // write out a modification
        while (true) {
          String left = "";
          String right = "";
          if (fromLineNum <= (delEnd)) {
            left = fromLines[fromLineNum];
            fromLineNum++;
          }
          if (toLineNum <= (addEnd)) {
            right = toLines[toLineNum];
            toLineNum++;
          }
          diffOut.append("[D] ");
          diffOut.append(lineNumber(lnf, fromLineNum));
          diffOut.append(Strings.padEnd(left, maxStrWidth, ' '));
          diffOut.append("| ");
          diffOut.append(lineNumber(lnf, toLineNum));
          diffOut.append(right).append("\n");

          if ((fromLineNum > (delEnd)) && (toLineNum > (addEnd))) {
            break;
          }
        }
      }

    }

    //we've finished displaying the diffs, now we just need to run out all the remaining unchanged lines
    while (!onlyDiffs) {
      String left = "";
      String right = "";
      if (fromLineNum < fromLines.length) {
        left = fromLines[fromLineNum];
      }
      if (fromLineNum <= fromLines.length) {
        fromLineNum++;
      }
      if (toLineNum < toLines.length) {
        right = toLines[toLineNum];
      }
      if (toLineNum <= toLines.length) {
        toLineNum++;
      }
      diffOut.append("    ");
      diffOut.append(lineNumber(lnf, fromLineNum));
      diffOut.append(Strings.padEnd(left, maxStrWidth, ' '));
      diffOut.append("  "); // no operator to display
      diffOut.append(lineNumber(lnf, toLineNum));
      diffOut.append(right).append("\n");

      if ((fromLineNum > fromLines.length) && (toLineNum > toLines.length)) {
        break;
      }
    }

    return diffOut.toString();
  }

  private static int maxLength(String... lines) {
    int maxLength = 0;

    for (String line : lines) {
      if (maxLength < line.length()) {
        maxLength = line.length();
      }
    }
    return maxLength;
  }

  private static String lineNumber(String format, int lineNumber) {
    return String.format(format, lineNumber);
  }

}
