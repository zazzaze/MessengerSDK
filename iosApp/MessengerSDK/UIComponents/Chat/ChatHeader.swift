import SwiftUI
import SwiftUIRouter

protocol ChatHeaderViewModel: ObservableObject {
    var chatTitle: String { get }
}

struct ChatHeader<ViewModel>: View where ViewModel: ChatHeaderViewModel {
    @EnvironmentObject var navigator: Navigator
    @ObservedObject var viewModel: ViewModel
    var body: some View {
        HStack(alignment: .center) {
            Button(action: { navigator.goBack() }) {
                Image(systemName: "chevron.backward")
                    .tint(.blue)
            }
            .padding(.leading, 20)
            .padding(.vertical, 15)
            Spacer()
            VStack {
                Text(viewModel.chatTitle)
                    .font(.headline)
            }
            .padding(.leading, -20)
            Spacer()
        }
        .padding(.top, 50)
        .border(width: 1, edges: [.bottom], color: .gray)
    }
}

struct ChatHeader_Previews: PreviewProvider {
    private final class ViewModel: ChatHeaderViewModel {
        init(chatTitle: String) {
            self.chatTitle = chatTitle
        }

        var chatTitle: String
    }

    static var previews: some View {
        ChatHeader(viewModel: ViewModel(chatTitle: "Test"))
            .previewLayout(.sizeThatFits)
    }
}
