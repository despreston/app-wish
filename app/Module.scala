import services.Twitter._
import com.google.inject.AbstractModule

/**
  * Created by despreston on 11/7/16.
  */

class Module extends AbstractModule {
  override def configure() = {
    bind(classOf[TwitterStreamer]).to(classOf[TwitterStreamerImpl]).asEagerSingleton()
  }
}
