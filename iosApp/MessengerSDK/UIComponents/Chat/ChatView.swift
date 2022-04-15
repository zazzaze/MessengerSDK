import SwiftUI
import SwiftUIRouter

struct ChatView: View {
    @EnvironmentObject private var navigator: Navigator
    let viewModel: ChatListViewModelImpl
    private let backgroundColor = Color(red: 249.0/255, green: 250.0/255, blue: 174.0/255)
    var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .center, spacing: 0) {
                if navigator.canGoBack {
                    ChatHeader(viewModel: viewModel)
                        .background(.white)
                }
                ChatList(viewModel: viewModel)
                    .padding(.top, geometry.safeAreaInsets.top)
                MessageTextInput(onSendMessageTap: viewModel.sendMessage(content:))
                    .padding(.bottom,40)
                    .background(.white)
                    .border(width: 0.5, edges: [.top], color: .black)
            }
            .background(backgroundColor)
            .ignoresSafeArea()
        }
    }
}

//struct ChatView_Previews: PreviewProvider {
//    static var previews: some View {
//        ChatView()
//    }
//}
