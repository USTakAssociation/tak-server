package tak.httpHandlers;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.zip.DataFormatException;

import com.sun.net.httpserver.HttpExchange;

import tak.Seek;
import tak.SeekDto;

public class AddSeekHandler extends JsonHttpHandler {
    @Override
    public SeekDto PUT(HttpExchange t) throws IOException, DataFormatException {
        SeekDto seekDto = jsonMapper.readValue(t.getRequestBody(), SeekDto.class);
        logger.log(Level.INFO, String.format("Successfully parsed DTO %s", seekDto.toString()));

        return Seek.newSeek(seekDto).toDto();
    }

    @Override
    public Collection<SeekDto> GET(HttpExchange httpExchange) {
        return Seek.getList();
    }
}
