package andreyorst;

import java.io.Reader;
import java.io.FilterReader;
import java.io.IOException;

/// Reads JSON discarding colons and unnecessary space characters.
/// Effectively, transforms JSON to a semi-valid EDN
public class CheatReader extends FilterReader {
    private boolean inString = false;
    private boolean escaped = false;
    private int next = -1;

    @Override
    public int read() throws IOException {
		for (int read;;) {
			read = (next != -1) ? next : super.read();
			next = -1;
			switch (read) {
				case 34: // "
					inString = inString ? escaped : true;
					break;
				case 92: // \
					if (!escaped) {
						int more = super.read();
						switch (more) {
							case 34: case 39: case 92:
							case 98: case 102: case 110:
							case 114: case 116: case 117:
								escaped = true;
								next = more;
								return read;
						}
						// invalid escape sequences are converted to
						// the escaped character
						read = more;
					}
					break;
				case 58: // :
				case 32: case 10: case 9: // spaces
					escaped = false;
					if (inString) return read;
					continue;
			}
			escaped = false;
			return read;
		}
    }

    public CheatReader(Reader reader) {
		super(reader);
    }
}
