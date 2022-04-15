import SwiftUI
import MessengerSDK

struct ContentView: View {
    let sdk: MessengerSDK
    var body: some View {
        sdk.buildMessengerView()
            .navigationBarHidden(true)
    }
}

//struct ContentView_Previews: PreviewProvider {
//    static var previews: some View {
//        ContentView()
//    }
//}
