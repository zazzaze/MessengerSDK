import SwiftUI
import SwiftUIRouter
import shared

public protocol ChatsListViewModel: ObservableObject {
    var chatsList: [Chat] { get }
}

public struct ChatsList<ViewModel>: View where ViewModel: ChatsListViewModel {
    @ObservedObject var viewModel: ViewModel
    public var body: some View {
        if viewModel.chatsList.isEmpty {
            Text("No available chats yet")
        } else {
            List(viewModel.chatsList, id: \.id) { chat in
                NavLink(to: "../chat/\(chat.id)") {
                    ChatCell(
                        model: .init(
                            image: nil,
                            title: chat.title,
                            message: chat.lastMessage?.asText(),
                            unreadMessagesCount: 0
                        )
                    )
                }
            }
            .listStyle(.plain)
        }
    }

    public init(viewModel: ViewModel) {
        self.viewModel = viewModel
    }
}

#if DEBUG
struct ChatsList_Previews: PreviewProvider {
    static var previews: some View {
        ChatsList(
            viewModel: ChatListViewModelPreview(
                chatsList: [
                    Chat.SimpleChat(
                        id: "1",
                        title: "ABC",
                        lastMessage: nil
                    )
                ]
            )
        )
    }
}

private final class ChatListViewModelPreview: ChatsListViewModel {
    var chatsList: [Chat]

    init(chatsList: [Chat]) {
        self.chatsList = chatsList
    }
}
#endif
