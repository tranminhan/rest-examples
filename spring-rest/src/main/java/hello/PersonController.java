package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by an.tranminh on 9/12/14.
 */

@Controller
public class PersonController
{
    static final Logger LOG = LoggerFactory.getLogger(PersonController.class);

    /*
     * Test posting with Chrome Advanced REST Client: {"name":"name", "age":"20", "city":"Sydney"}
     */
    @RequestMapping(value = "/addPerson", method = RequestMethod.POST)
    @ResponseBody
    public String addPerson(@RequestBody Person person)
    {
        LOG.info(person.toString());
        return "Success";
    }

    @RequestMapping(value = "getPerson", method = RequestMethod.GET)
    @ResponseBody
    public Person getPerson()
    {
        return new Person();
    }

}
